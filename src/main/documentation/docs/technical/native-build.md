# Build natif

ARIG Cipher peut être compilé en **binaire natif autonome** grâce à [GraalVM Native Image](https://www.graalvm.org/native-image/). Le binaire produit ne nécessite pas de JVM installée sur la machine cible et démarre en quelques millisecondes.

---

## Prérequis

### GraalVM

| Composant | Version requise | Notes |
|---|---|---|
| GraalVM | 25+ (basé sur Java 25) | Distribution Oracle GraalVM ou GraalVM CE |
| `native-image` | Inclus dans GraalVM | Vérifier avec `native-image --version` |
| Mémoire RAM | 8 Go minimum recommandés | La compilation native est très gourmande |

!!! warning "GraalVM ≠ JDK standard"
    Le build natif nécessite **GraalVM**, pas un JDK standard. La variable `JAVA_HOME` doit pointer vers votre installation GraalVM. Un JDK OpenJDK classique ne dispose pas de la commande `native-image`.

### Vérification de l'environnement

```bash
# Vérifier que JAVA_HOME pointe bien vers GraalVM
echo $JAVA_HOME
# Ex : /Library/Java/JavaVirtualMachines/graalvm-jdk-25/Contents/Home

# Vérifier la disponibilité de native-image
native-image --version
# Ex : native-image 25.0.0 2025-03-18

# Vérifier que Gradle utilise le bon JDK
./gradlew -version
```

### Dépendances système

=== "Linux (x86_64 / ARM64)"
    ```bash
    # Debian / Ubuntu
    sudo apt-get install build-essential zlib1g-dev libgtk-3-dev libx11-dev

    # Fedora / RHEL
    sudo dnf install gcc glibc-devel zlib-devel gtk3-devel
    ```

=== "macOS (ARM64)"
    ```bash
    # Xcode Command Line Tools (requis)
    xcode-select --install
    ```

=== "Windows (x86_64)"
    - **Visual Studio Build Tools** avec le workload "Desktop development with C++"
    - Lancer le build depuis une **Developer Command Prompt**

---

## Compilation

### Commande de build

```bash
./gradlew distNative
```

Sur Windows :

```powershell
.\gradlew.bat distNative
```

### Durée

La compilation native prend typiquement entre **5 et 20 minutes** selon la plateforme et la puissance de la machine. C'est normal — GraalVM effectue une analyse de l'intégralité du graphe d'appels de l'application.

!!! tip "Indicateur de progression"
    GraalVM affiche des étapes numérotées pendant la compilation (`[1/7] Initializing...`, `[2/7] Performing analysis...`, etc.). Si vous restez bloqué à l'étape d'analyse longtemps, c'est attendu.

### Sortie

Le binaire produit est placé dans :

```
build/native/nativeCompile/
└── lucifer          # Linux / macOS
└── lucifer.exe      # Windows
```

Pour packager et distribuer :

```bash
./gradlew distNative
# Produit une archive distribuable dans build/distributions/
```

---

## Plateformes supportées

| Plateforme | Architecture | Statut CI | Runner GitHub Actions |
|---|---|---|---|
| Linux | x86_64 | Supporté | `ubuntu-latest` |
| Linux | ARM64 | Supporté | `ubuntu-24.04-arm` |
| Windows | x86_64 | Supporté | `windows-latest` |
| macOS | ARM64 (Apple Silicon) | Supporté | `macos-latest` |

!!! note "Cross-compilation non supportée"
    GraalVM Native Image ne supporte **pas** la cross-compilation : vous devez compiler sur la même plateforme que celle ciblée. C'est pourquoi la CI utilise 4 runners distincts.

---

## CI/CD GitHub Actions

### Déclencheurs

Le workflow de packaging natif est déclenché dans deux cas :

| Déclencheur | Description |
|---|---|
| **Tag `v*`** | Tout tag Git commençant par `v` (ex : `v1.0.0`, `v2.1.3`) |
| **`workflow_dispatch`** | Déclenchement manuel depuis l'interface GitHub Actions |

### Structure du workflow

```yaml
name: Native Build & Release

on:
  push:
    tags:
      - 'v*'
  workflow_dispatch:

jobs:
  build-native:
    strategy:
      matrix:
        include:
          - os: ubuntu-latest
            arch: x86_64
            platform: linux-x86_64
          - os: ubuntu-24.04-arm
            arch: arm64
            platform: linux-arm64
          - os: windows-latest
            arch: x86_64
            platform: windows-x86_64
          - os: macos-latest
            arch: arm64
            platform: macos-arm64

    runs-on: ${{ matrix.os }}

    steps:
      - uses: actions/checkout@v4

      - name: Setup GraalVM
        uses: graalvm/setup-graalvm@v1
        with:
          java-version: '25'
          distribution: 'graalvm'

      - name: Build native image
        run: ./gradlew distNative

      - name: Upload artifact
        uses: actions/upload-artifact@v4
        with:
          name: lucifer-${{ matrix.platform }}
          path: build/distributions/
```

### Créer une release

```bash
# Tagger la version
git tag v1.2.0
git push origin v1.2.0

# GitHub Actions se déclenche automatiquement sur les 4 plateformes
# Les artefacts sont disponibles dans l'onglet "Actions" de GitHub
```

---

## Considérations JavaFX en mode natif

### Bibliothèques natives JavaFX

JavaFX utilise des bibliothèques natives (OpenGL, GTK, Win32...) qui doivent être incluses dans le binaire natif ou distribuées à côté. Le plugin Gradle `org.openjfx.javafxplugin` gère la plupart de ces besoins, mais certains modules nécessitent une attention particulière.

### Module `javafx-media`

!!! warning "javafx-media et GraalVM"
    Le module `javafx-media` (lecture audio/vidéo) est **particulièrement délicat** avec GraalVM Native Image. Il repose sur des bibliothèques natives spécifiques à chaque OS (GStreamer sur Linux, AVFoundation sur macOS, DirectShow sur Windows) qui ne sont pas toutes supportées en mode natif.

    Si ARIG Cipher n'utilise pas de fonctionnalités audio/vidéo, **exclure `javafx-media`** du classpath simplifiera considérablement le build natif.

### Configuration de réflexion

GraalVM Native Image nécessite une déclaration explicite des classes utilisées par réflexion. Spring Boot et JavaFX en utilisent abondamment. Les configurations sont généralement auto-détectées via l'agent de trace GraalVM :

```bash
# Générer la configuration de réflexion en mode agent (JVM normal)
./gradlew bootRun \
  -Pagent \
  -Dspring.aot.enabled=true
# Les fichiers de configuration sont générés dans src/main/resources/META-INF/native-image/
```

### Spring Boot AOT (Ahead-Of-Time)

Spring Boot 4.x intègre un support natif via la compilation **AOT** qui pré-génère le code de configuration Spring (évitant la réflexion à l'exécution). Ce mécanisme est automatiquement activé par la tâche `distNative`.

---

## Taille et performance du binaire

| Métrique | JVM (bootRun) | Natif (distNative) |
|---|---|---|
| Démarrage | ~3-8 secondes | ~100-300 ms |
| Taille du runtime | JVM (~300 Mo) + JAR | ~50-150 Mo (tout inclus) |
| Consommation mémoire au démarrage | ~150-300 Mo | ~30-80 Mo |

!!! tip "Distribution sans JVM"
    Un binaire natif peut être distribué à des utilisateurs **sans JVM installée**. C'est l'avantage principal pour une application desktop comme ARIG Cipher.
