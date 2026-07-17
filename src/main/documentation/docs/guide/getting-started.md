# Démarrage rapide

Ce guide vous permet de lancer ARIG Cipher en quelques minutes, que vous souhaitiez simplement exécuter l'application ou contribuer au projet.

---

## Prérequis

### Requis

| Outil | Version minimale | Notes |
|---|---|---|
| **JDK** | Java 25+ | OpenJDK ou Oracle JDK |
| **Git** | Toute version récente | Pour cloner le dépôt |

!!! warning "Java 25 obligatoire"
    L'application utilise des fonctionnalités et une syntaxe spécifiques à **Java 25**. Les versions antérieures ne sont pas supportées. Vérifiez votre version avec `java --version`.

### Optionnel — Build natif uniquement

| Outil | Version recommandée | Notes |
|---|---|---|
| **GraalVM** | 25+ (basé sur Java 25) | Nécessaire pour `./gradlew distNative` uniquement |

!!! tip "GraalVM pas nécessaire pour le développement"
    Pour développer, tester ou simplement utiliser l'application, le JDK standard suffit. GraalVM n'est requis que pour produire un exécutable natif indépendant de la JVM.

---

## Installation

### 1. Cloner le dépôt

```bash
git clone https://github.com/arig-robotique/lucifer.git
cd lucifer
```

### 2. Vérifier l'environnement Java

```bash
java --version
# Attendu : openjdk 25.x.x ... ou similaire

./gradlew --version
# Affiche la version de Gradle configurée pour le projet
```

### 3. Lancer l'application (mode développement)

```bash
./gradlew bootRun
```

!!! note "Premier lancement"
    Gradle téléchargera automatiquement les dépendances lors du premier lancement. Comptez quelques minutes selon votre connexion internet.

Sur Windows, utilisez :

```powershell
.\gradlew.bat bootRun
```

---

## Première utilisation

Au démarrage, l'application affiche un **splash screen** avec le logo animé ARIG Robotique. Vous pouvez :

- **Attendre** : le splash disparaît automatiquement après 5 secondes avec un fondu
- **Cliquer** : fermer immédiatement le splash screen

L'interface principale s'ouvre ensuite, permettant de chiffrer ou déchiffrer des fichiers.

---

## Structure du projet

```
lucifer/
├── src/
│   └── main/
│       ├── java/
│       │   └── fr/arig/cipher/
│       │       ├── LuciferApplication.java   # Point d'entrée Spring Boot
│       │       ├── LuciferFxApp.java          # Application JavaFX
│       │       ├── view/
│       │       │   ├── SplashView.java        # Splash screen SVG animé
│       │       │   └── MainView.java          # Interface principale
│       │       └── service/
│       │           ├── CipherService.java     # Chiffrement / déchiffrement
│       │           └── Lucifer.java           # Implémentation Feistel
│       └── resources/
│           └── logo_animated.svg             # SVG du splash screen
├── build.gradle.kts                          # Configuration Gradle
├── gradlew / gradlew.bat                     # Wrapper Gradle
└── docs/                                     # Documentation MkDocs
```

---

## Commandes utiles

```bash
# Lancer en mode développement
./gradlew bootRun

# Compiler sans exécuter
./gradlew build

# Lancer les tests
./gradlew test

# Produire l'exécutable natif (requiert GraalVM)
./gradlew distNative

# Nettoyer le build
./gradlew clean
```

---

## Étapes suivantes

- [Guide d'utilisation complet](usage.md) — chiffrer et déchiffrer des fichiers pas à pas
- [Architecture](../technical/architecture.md) — comprendre l'intégration Spring Boot + JavaFX
- [Build natif](../technical/native-build.md) — produire un binaire autonome avec GraalVM
