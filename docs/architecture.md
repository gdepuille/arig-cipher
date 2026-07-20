# Architecture

ARIG Cipher combine deux frameworks qui ne sont pas conçus nativement pour fonctionner ensemble : **Spring Boot** (injection de dépendances, cycle de vie applicatif) et **JavaFX** (interface graphique desktop). Cette page décrit le pattern d'intégration retenu et le cycle de vie complet de l'application.

---

## Vue d'ensemble

```
┌─────────────────────────────────────────────────────────────────┐
│                        JVM Process                              │
│                                                                 │
│  ┌──────────────────────────┐   ┌───────────────────────────┐  │
│  │   Spring Boot Context    │   │    JavaFX Application      │  │
│  │                          │   │                            │  │
│  │  LuciferApplication      │──▶│  LuciferFxApp              │  │
│  │  (CommandLineRunner)     │   │  (Application.launch())    │  │
│  │                          │   │                            │  │
│  │  @Service                │   │  SplashView                │  │
│  │  CipherService ──────────┼───┼──▶ MainView                │  │
│  │  Lucifer (algo)          │   │                            │  │
│  └──────────────────────────┘   └───────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
```

---

## Pattern d'intégration Spring Boot + JavaFX

### Problématique

JavaFX exige que son point d'entrée (`Application.launch()`) soit appelé depuis le **thread principal** de la JVM, et il bloque ce thread jusqu'à la fermeture de la fenêtre. Spring Boot, de son côté, démarre son propre contexte et ses threads.

La solution retenue dans ARIG Cipher est le **pattern `CommandLineRunner`** :

### `LuciferApplication` — Point d'entrée

```java
@SpringBootApplication
public class LuciferApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(LuciferApplication.class, args);
    }

    @Override
    public void run(String... args) {
        // Spring Boot est démarré, le contexte est prêt.
        // On délègue au thread JavaFX.
        Application.launch(LuciferFxApp.class, args);
        // Application.launch() bloque jusqu'à la fermeture de JavaFX.
        // Quand JavaFX se ferme, on quitte proprement.
        System.exit(0);
    }
}
```

!!! note "Pourquoi `System.exit(0)` ?"
    Spring Boot démarre des threads non-daemon (ex. Tomcat embedded). Sans `System.exit(0)`, la JVM ne se terminerait pas après la fermeture de la fenêtre JavaFX. L'appel explicite garantit une sortie propre.

### `LuciferFxApp` — Application JavaFX

```java
public class LuciferFxApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Récupération du contexte Spring via un holder statique
        CipherService cipherService = SpringContext.getBean(CipherService.class);

        // Affichage du splash screen
        SplashView splash = new SplashView();
        splash.show(primaryStage, () -> {
            // Callback après fermeture du splash
            MainView mainView = new MainView(cipherService);
            mainView.show(primaryStage);
        });
    }
}
```

!!! tip "Accès au contexte Spring depuis JavaFX"
    Puisque `LuciferFxApp` est instanciée par JavaFX (et non par Spring), elle n'est pas un bean Spring. Pour accéder aux services Spring depuis JavaFX, on utilise un **holder statique** qui conserve une référence au `ApplicationContext` Spring, initialisé dans `LuciferApplication.run()`.

---

## Cycle de vie de l'application

```
main()
  │
  ▼
SpringApplication.run()         ← Démarre le contexte Spring,
  │                               scanne les @Service, @Component, etc.
  │
  ▼
CommandLineRunner.run()          ← Appelé par Spring une fois le contexte prêt
  │
  ▼
Application.launch(LuciferFxApp) ← Initialise JavaFX, crée le thread FX
  │                               (bloquant : reste ici jusqu'à fermeture)
  │
  ▼
LuciferFxApp.start(Stage)        ← Thread JavaFX Application Thread
  │
  ├──▶ SplashView.show()         ← WebView + SVG animé, 5s ou clic
  │         │
  │         ▼ (callback)
  └──▶ MainView.show()           ← Interface principale
            │
            ▼ (fermeture fenêtre)
        Platform.exit()          ← Arrêt JavaFX
            │
            ▼
        Application.launch() retourne
            │
            ▼
        System.exit(0)           ← Arrêt JVM + Spring context
```

---

## Structure des packages

```
fr.arig.cipher
├── LuciferApplication.java      # @SpringBootApplication + CommandLineRunner
├── LuciferFxApp.java            # javafx.application.Application
│
├── view/
│   ├── SplashView.java          # Splash screen (WebView + SVG)
│   └── MainView.java            # Interface principale (programmatique, pas FXML)
│
└── service/
    ├── CipherService.java       # @Service : encrypt() / decrypt()
    └── Lucifer.java             # Implémentation pure du chiffre Feistel
```

### Choix : pas de FXML

L'interface principale (`MainView`) est construite **programmatiquement** en Java, sans fichier FXML. Ce choix simplifie l'intégration avec GraalVM native image (les fichiers FXML nécessitent une configuration de réflexion supplémentaire).

---

## Composants principaux

### `SplashView`

- Utilise un **`WebView`** JavaFX pour afficher un SVG animé (`logo_animated.svg`)
- L'animation SVG se joue pendant 5 secondes
- Un `Timeline` JavaFX déclenche une transition `FadeTransition` à la fin
- Un handler `onMouseClicked` permet de fermer immédiatement

### `MainView`

- Construction programmatique via `VBox`, `HBox`, `Button`, `Label`
- Thème sombre appliqué via CSS inline ou feuille de style embarquée
- Couleur principale : `#249D2E` (vert ARIG Robotique)
- Délègue les opérations de fichiers à `CipherService`

### `CipherService`

- Bean Spring (`@Service`), injectable
- `encrypt(Path srcPath, Path destDir)` : chiffre et écrit le fichier `.arig`
- `decrypt(Path srcPath, Path destDir)` : lit l'en-tête, déchiffre, restaure le fichier
- Délègue le chiffrement byte-level à `Lucifer`

### `Lucifer`

- Implémentation pure sans état (stateless)
- Opère sur un seul octet à la fois via un réseau de Feistel à 4 rounds
- Clé fixe : `[b0=1, b1=4, b2=3, b3=2]`
- Voir [Algorithme Lucifer](cipher.md) pour le détail de l'implémentation
