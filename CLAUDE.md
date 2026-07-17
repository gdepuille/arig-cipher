# ARIG Cipher — Guide Claude Code

## Commandes de build

| Commande | Usage |
|---|---|
| `./gradlew bootRun` | Lancer en mode dev (JVM, hot reload) |
| `./gradlew build` | Compiler + tests |
| `./gradlew test` | Tests unitaires uniquement |
| `./gradlew distNative` | Build exécutable natif GraalVM |
| `./gradlew clean` | Nettoyer le répertoire `build/` |

---

## Structure du projet

```
lucifer/
├── build.gradle.kts                  Configuration Gradle (JavaFX platform-specific, GraalVM)
├── settings.gradle.kts
├── src/
│   └── main/
│       ├── java/org/arig/lucifer/
│       │   ├── LuciferApplication.java       Spring Boot main + CommandLineRunner
│       │   ├── crypt/
│       │   │   └── Lucifer.java              !! NE PAS MODIFIER — algorithme historique
│       │   └── fx/
│       │       ├── LuciferFxApp.java         javafx.application.Application
│       │       ├── MainView.java             IHM principale (programmatique, sans FXML)
│       │       ├── SplashView.java           Splash screen WebView + SVG animé
│       │       └── CipherService.java        Chiffrement / déchiffrement + en-tête .arig
│       └── resources/org/arig/lucifer/fx/
│           ├── logo.png
│           ├── logo_animated.svg
│           └── styles.css                    Palette Catppuccin + variables CSS ARIG
```

---

## Architecture Spring Boot + JavaFX

Le démarrage suit le pattern `CommandLineRunner` + `Application.launch()` :

1. `LuciferApplication` est le point d'entrée Spring Boot (`@SpringBootApplication`).
2. Son `CommandLineRunner` appelle `Application.launch(LuciferFxApp.class, args)` pour démarrer le thread JavaFX.
3. `LuciferFxApp` (extends `javafx.application.Application`) initialise la scène, affiche `SplashView` puis charge `MainView`.
4. `CipherService` est un bean Spring (`@Service`) injecté dans `MainView`.

Ne pas appeler `SpringApplication.run()` depuis `LuciferFxApp` — le contexte Spring doit être créé avant le lancement JavaFX.

---

## Format fichier `.arig`

Les fichiers chiffrés portent l'extension `.arig`. Structure de l'en-tête :

```
[ 4 octets big-endian ]  longueur du nom de fichier original (int)
[ N octets UTF-8       ]  nom de fichier original (N = valeur ci-dessus)
[ reste du fichier     ]  données chiffrées par Lucifer (blocs de 8 octets, PKCS#5 padding)
```

Le déchiffrement lit d'abord l'en-tête pour récupérer le nom d'origine, puis applique Lucifer en sens inverse.

---

## Convention CSS

- Toute la palette est définie via des variables CSS dans `styles.css` (`:root { --color-base: ...; }`).
- Ne pas utiliser de styles inline (`node.setStyle(...)`) sauf pour des valeurs dynamiques calculées à l'exécution (ex. progression d'une barre).
- Classes CSS à préférer : `.arig-button`, `.arig-label`, `.arig-card`, `.arig-drop-zone`.
- Le thème suit la palette Catppuccin Mocha enrichie de la variable `--color-arig: #4CAF50`.

---

## Points d'attention critiques

### Lucifer.java — NE PAS MODIFIER
`org.arig.lucifer.crypt.Lucifer` implémente l'algorithme historique avec la clé codée en dur `[1,4,3,2]`. Toute modification changerait le comportement de chiffrement et rendrait illisibles les fichiers `.arig` existants. Les bugs éventuels dans cet algorithme sont intentionnellement conservés tels quels.

### Dépendance `javafx-media` requise pour WebView
`SplashView` utilise `WebView`, qui dépend en interne de `javafx-media` même si aucun média n'est joué. La dépendance `javafx-media` doit rester dans `build.gradle.kts`, sinon le splash screen lève une `RuntimeException` au chargement.

### Dépendances JavaFX platform-specific
Les artefacts JavaFX sont déclinés par plateforme (`linux`, `win`, `mac`, `mac-aarch64`). Le `build.gradle.kts` détecte automatiquement la plateforme cible via `os.name` / `os.arch` et sélectionne le bon classifier. Ne pas ajouter de classifier en dur — cela casserait les builds cross-platform GitHub Actions.

---

## Avertissements Java 25 normaux (non bloquants)

Ces messages peuvent apparaître au démarrage en mode JVM classpath ; ils sont attendus et sans impact fonctionnel :

```
WARNING: Unsupported JavaFX configuration: classes were loaded from ...
WARNING: sun.misc.Unsafe::objectFieldOffset ... is not supported
```

Ils disparaissent en build natif GraalVM. Ne pas tenter de les supprimer par des flags JVM supplémentaires — cela peut introduire des effets de bord.
