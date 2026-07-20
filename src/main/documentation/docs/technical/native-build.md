# Build & Distribution

ARIG Cipher propose deux formats de distribution multi-plateforme, produits automatiquement par GitHub Actions. Les deux reposent sur **Liberica JDK** de BellSoft.

---

## Vue d'ensemble

| Format | Outil | Prérequis utilisateur | Taille |
|---|---|---|---|
| **Installer** (dmg/deb/msi) | jpackage | Aucun | ~150-200 Mo |
| **Binaire autonome** | Liberica NIK | Aucun | ~50-150 Mo |

Les deux formats ne nécessitent **aucune JVM préinstallée** sur la machine cible.

---

## Installers natifs (jpackage)

Les installers embarquent un JRE Liberica Full (avec JavaFX) et s'installent comme une application native classique.

### Plateformes et artefacts

| Plateforme | Artefact | Format |
|---|---|---|
| macOS ARM64 | `arig-cipher-macos-arm64.dmg` | Image disque |
| Linux x86\_64 | `arig-cipher-linux-x86_64.deb` | Paquet Debian |
| Linux ARM64 | `arig-cipher-linux-arm64.deb` | Paquet Debian |
| Windows x86\_64 | `arig-cipher-windows-x86_64.msi` | Installeur MSI |

### Build local

Requiert **Liberica JDK Full 25** (inclut OpenFX).

```bash
./gradlew distJpackage
```

L'installeur est produit dans `dist/`.

=== "macOS"
    ```bash
    open dist/"ARIG Cipher-0.0.1.dmg"
    ```

=== "Linux"
    ```bash
    sudo dpkg -i dist/arig-cipher-*.deb
    ```

=== "Windows"
    Double-cliquer sur le `.msi` généré dans `dist/`.

---

## Binaires autonomes (Liberica NIK)

Les binaires natifs sont compilés avec **Liberica Native Image Kit** (NIK), qui intègre une prise en charge native de JavaFX (OpenFX). Contrairement à GraalVM Community, le NIK de BellSoft résout nativement le `QuantumToolkit` et les bibliothèques graphiques JavaFX.

### Plateformes et artefacts

| Plateforme | Artefact |
|---|---|
| macOS ARM64 | `arig-cipher-macos-arm64-bin` |
| Linux x86\_64 | `arig-cipher-linux-x86_64-bin` |
| Linux ARM64 | `arig-cipher-linux-arm64-bin` |
| Windows x86\_64 | `arig-cipher-windows-x86_64-bin.exe` |

### Build local

Requiert **Liberica NIK 25** (avec `native-image`).

```bash
# Vérifier l'environnement
native-image --version
# Ex : native-image 25.0.x BellSoft Liberica NIK

./gradlew distNative
```

Le binaire est produit dans `dist/arig-cipher` (ou `dist/arig-cipher.exe` sur Windows).

### Dépendances système (Linux uniquement)

Sur Linux, les bibliothèques natives JavaFX doivent être présentes sur la machine de build :

```bash
sudo apt-get install -y \
  libasound2-dev libavcodec-dev libavformat-dev libavutil-dev \
  libgl-dev libgtk-3-dev libpango1.0-dev libxtst-dev
```

!!! note "Pas requis sur la machine cible"
    Ces bibliothèques sont nécessaires **au moment de la compilation** uniquement. Le binaire produit les embarque et fonctionne sans elles sur la machine de l'utilisateur.

---

## Durée de compilation

| Étape | Durée approximative |
|---|---|
| `bootJar` (installer) | 30-60 secondes |
| `nativeCompile` (binaire) | 5-20 minutes |

La compilation native est intensive en CPU et mémoire (8 Go RAM recommandés). Liberica NIK affiche des étapes numérotées (`[1/7] Initializing...`) pendant le processus.

---

## CI/CD GitHub Actions

### Déclencheurs

| Déclencheur | Description |
|---|---|
| **Tag `v*`** | Tout tag Git commençant par `v` (ex : `v1.0.0`) |
| **`workflow_dispatch`** | Déclenchement manuel depuis l'interface GitHub |

### Structure du workflow

Le workflow `.github/workflows/native-build.yml` contient trois jobs :

```
┌─────────────────────────────────────────────────────┐
│  Job : installer (4 runners en parallèle)            │
│  Liberica JDK Full 25 + jpackage                     │
│  → arig-cipher-*.dmg / .deb / .msi                  │
├─────────────────────────────────────────────────────┤
│  Job : native-binary (4 runners en parallèle)        │
│  Liberica NIK 25 + native-image                      │
│  → arig-cipher-*-bin / .exe                          │
├─────────────────────────────────────────────────────┤
│  Job : release (si tag v*)                           │
│  Attend les 2 jobs, crée la GitHub Release           │
│  → 8 artefacts attachés à la release                 │
└─────────────────────────────────────────────────────┘
```

### Créer une release

```bash
git tag v1.0.0
git push origin v1.0.0
# GitHub Actions se déclenche sur les 4 plateformes × 2 formats = 8 artefacts
```

---

## Performances comparées

| Métrique | JVM (`bootRun`) | Installer (jpackage) | Binaire (NIK) |
|---|---|---|---|
| Démarrage | ~3-8 secondes | ~1-3 secondes | ~100-300 ms |
| Taille distribution | JVM + JAR (~350 Mo) | ~150-200 Mo | ~50-150 Mo |
| Mémoire au démarrage | ~150-300 Mo | ~80-150 Mo | ~30-80 Mo |
| JVM requise | Oui | Non (incluse) | Non |

!!! tip "Quel format choisir ?"
    - **Installer** : pour une installation classique sur poste utilisateur, avec désinstalleur OS intégré.
    - **Binaire** : pour une distribution sans installeur — copier-coller et exécuter, idéal pour clé USB ou environnements restreints.