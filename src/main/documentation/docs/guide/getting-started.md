# Démarrage rapide

Ce guide vous permet de lancer ARIG Cipher en quelques minutes, que vous souhaitiez simplement exécuter l'application ou contribuer au projet.

---

## Prérequis

### Requis

| Outil | Version minimale | Notes |
|---|---|---|
| **Liberica JDK Full** | 25 | Inclut OpenFX (JavaFX) — [télécharger](https://bell-sw.com/pages/downloads/) |
| **Git** | Toute version récente | Pour cloner le dépôt |

!!! warning "Liberica JDK Full obligatoire"
    L'application utilise **JavaFX**. La version **Full** de Liberica JDK intègre OpenFX et est nécessaire pour `bootRun`. Un JDK standard (OpenJDK, Oracle) ne suffit pas sans les dépendances JavaFX natives.

    Vérifiez votre version avec `java --version` et que `javafx` est disponible.

### Optionnel — Build natif uniquement

| Outil | Version recommandée | Notes |
|---|---|---|
| **Liberica NIK** | 25 (basé sur Java 25) | Nécessaire pour `./gradlew distNative` uniquement |

!!! tip "Liberica NIK pas nécessaire pour le développement"
    Pour développer, tester ou simplement utiliser l'application, le **Liberica JDK Full** suffit. Le NIK (Native Image Kit) n'est requis que pour produire un binaire natif autonome.

---

## Installation

### 1. Cloner le dépôt

```bash
git clone https://github.com/gdepuille/arig-cipher.git
cd arig-cipher
```

### 2. Vérifier l'environnement Java

```bash
java --version
# Attendu : OpenJDK 25.x.x Liberica (Full) ou similaire

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
arig-cipher/
├── src/
│   └── main/
│       ├── java/org/arig/lucifer/
│       │   ├── LuciferApplication.java   # Point d'entrée Spring Boot
│       │   ├── fx/
│       │   │   ├── LuciferFxApp.java     # Application JavaFX
│       │   │   ├── SplashView.java       # Splash screen SVG animé
│       │   │   ├── MainView.java         # Interface principale
│       │   │   └── CipherService.java    # Chiffrement / déchiffrement
│       │   └── crypt/
│       │       └── Lucifer.java          # Algorithme Feistel (ne pas modifier)
│       ├── resources/org/arig/lucifer/fx/
│       │   ├── styles.css               # Palette Catppuccin + variables ARIG
│       │   ├── logo.png / logo.icns / logo.ico
│       │   └── logo_animated.svg        # SVG du splash screen
│       └── documentation/               # Sources MkDocs
├── build.gradle.kts                     # Configuration Gradle
└── gradlew / gradlew.bat                # Wrapper Gradle
```

---

## Commandes utiles

```bash
# Lancer en mode développement (JVM)
./gradlew bootRun

# Compiler + tests
./gradlew build

# Tests unitaires
./gradlew test

# Créer un installeur natif (dmg/deb/msi) — Liberica JDK Full
./gradlew distJpackage

# Créer un binaire autonome — Liberica NIK requis
./gradlew distNative

# Nettoyer le build
./gradlew clean
```

---

## Étapes suivantes

- [Guide d'utilisation complet](usage.md) — chiffrer et déchiffrer des fichiers pas à pas
- [Architecture](../technical/architecture.md) — comprendre l'intégration Spring Boot + JavaFX
- [Build & Distribution](../technical/native-build.md) — produire des installers et binaires natifs
