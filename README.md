# ARIG Cipher — Chiffrement Lucifer

[![Build](https://github.com/arig/lucifer/actions/workflows/build.yml/badge.svg)](https://github.com/arig/lucifer/actions)
[![Java 25](https://img.shields.io/badge/Java-25-blue?logo=openjdk)](https://openjdk.org/projects/jdk/25/)
[![Spring Boot 4.1](https://img.shields.io/badge/Spring%20Boot-4.1-6DB33F?logo=springboot)](https://spring.io/projects/spring-boot)
[![JavaFX 24](https://img.shields.io/badge/JavaFX-24-orange)](https://openjfx.io/)
[![License MIT](https://img.shields.io/badge/License-MIT-lightgrey)](LICENSE)

Application desktop de chiffrement de fichiers basée sur l'algorithme historique **Lucifer** (réseau de Feistel, 4 rounds, clé codée en dur `[1,4,3,2]`). Elle offre une interface graphique sombre construite avec JavaFX et pilotée par Spring Boot, avec un splash screen animé en SVG et une palette Catppuccin enrichie du vert ARIG.

---

## Fonctionnalités

- Chiffrement et déchiffrement de fichiers par l'algorithme Lucifer (Feistel 4 rounds)
- Préservation du nom de fichier original dans l'en-tête du fichier `.arig`
- Interface dark theme (palette Catppuccin + vert ARIG `#4CAF50`)
- Splash screen animé rendu via WebView (SVG)
- Build natif GraalVM multi-plateforme via GitHub Actions
- Sélection de fichier par glisser-déposer ou explorateur natif

---

## Prérequis

| Outil | Version minimale |
|---|---|
| JDK (OpenJDK ou GraalVM CE) | 25 |
| Gradle (wrapper fourni) | 8.x |
| GraalVM Native Image *(build natif uniquement)* | 25 |

---

## Démarrage rapide

```bash
# Cloner le dépôt
git clone https://github.com/arig/lucifer.git
cd lucifer

# Lancer en mode développement (JVM)
./gradlew bootRun
```

Les fichiers chiffrés portent l'extension `.arig`. Le fichier original peut être récupéré en le déposant à nouveau sur l'application.

---

## Build natif

L'exécutable natif est produit avec GraalVM Native Image. Il est distribué via GitHub Actions pour les quatre plateformes cibles :

| Plateforme | Runner GitHub Actions | Artefact |
|---|---|---|
| Linux x86\_64 | `ubuntu-latest` | `lucifer-linux-x86_64` |
| Linux ARM64 | `ubuntu-24.04-arm` | `lucifer-linux-arm64` |
| Windows x86\_64 | `windows-latest` | `lucifer-windows-x86_64.exe` |
| macOS ARM64 | `macos-latest` | `lucifer-macos-arm64` |

```bash
# Build natif local (nécessite GraalVM avec native-image installé)
./gradlew distNative
```

Le binaire produit est autonome et ne nécessite pas de JVM sur la machine cible.

---

## Architecture

```
LuciferApplication          Spring Boot main + CommandLineRunner
    └── LuciferFxApp        javafx.application.Application (lancé via Application.launch())
            ├── SplashView  Splash screen WebView + logo_animated.svg
            └── MainView    IHM principale (programmatique, sans FXML)
                    └── CipherService   Chiffrement / déchiffrement + gestion en-tête .arig
                            └── Lucifer Algorithme Feistel (NE PAS MODIFIER)
```

Les dépendances JavaFX sont détectées automatiquement selon la plateforme cible dans `build.gradle.kts`. Spring Boot gère l'injection de dépendances ; JavaFX prend ensuite la main sur le thread UI via le `CommandLineRunner`.

---

## License

Ce projet est distribué sous licence [MIT](LICENSE).
