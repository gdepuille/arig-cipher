# ARIG Cipher — Chiffrement Lucifer

[![Build](https://github.com/gdepuille/arig-cipher/actions/workflows/native-build.yml/badge.svg)](https://github.com/gdepuille/arig-cipher/actions)
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
- Sélection de fichier par glisser-déposer ou explorateur natif
- Distribution multi-plateforme : installers natifs (dmg/deb/msi) **et** binaires autonomes

---

## Prérequis

| Outil | Version minimale | Notes |
|---|---|---|
| Liberica JDK Full | 25 | Inclut OpenFX — pour `bootRun` et `distJpackage` |
| Gradle (wrapper fourni) | 9.x | Aucune installation requise |
| Liberica NIK *(build natif uniquement)* | 25 | Pour `distNative` uniquement |

---

## Démarrage rapide

```bash
# Cloner le dépôt
git clone https://github.com/gdepuille/arig-cipher.git
cd arig-cipher

# Lancer en mode développement (JVM)
./gradlew bootRun
```

Les fichiers chiffrés portent l'extension `.arig`. Le fichier original peut être récupéré en le déposant à nouveau sur l'application.

---

## Distribution

Deux formats de distribution sont produits par GitHub Actions :

### Installers (jpackage)

Incluent un JRE Liberica — aucune dépendance requise sur la machine cible.

| Plateforme | Artefact | Format |
|---|---|---|
| macOS ARM64 | `arig-cipher-macos-arm64.dmg` | Image disque |
| Linux x86\_64 | `arig-cipher-linux-x86_64.deb` | Paquet Debian |
| Linux ARM64 | `arig-cipher-linux-arm64.deb` | Paquet Debian |
| Windows x86\_64 | `arig-cipher-windows-x86_64.msi` | Installeur MSI |

### Binaires autonomes (Liberica NIK)

Exécutables seuls, sans JVM ni installeur.

| Plateforme | Artefact |
|---|---|
| macOS ARM64 | `arig-cipher-macos-arm64-bin` |
| Linux x86\_64 | `arig-cipher-linux-x86_64-bin` |
| Linux ARM64 | `arig-cipher-linux-arm64-bin` |
| Windows x86\_64 | `arig-cipher-windows-x86_64-bin.exe` |

```bash
# Build installeur local (Liberica JDK Full requis)
./gradlew distJpackage

# Build binaire autonome local (Liberica NIK requis)
./gradlew distNative
```

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