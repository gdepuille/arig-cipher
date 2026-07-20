# Installation

ARIG Cipher est disponible en deux formats : un **installeur** qui intègre tout le nécessaire, et un **binaire autonome** pour les utilisateurs qui préfèrent ne rien installer.

---

## Télécharger

Rendez-vous sur la [page des releases](https://github.com/gdepuille/arig-cipher/releases/latest) et téléchargez le fichier correspondant à votre système.

---

## Installeur (recommandé)

=== "macOS (Apple Silicon)"

    1. Téléchargez `arig-cipher-macos-arm64.dmg`
    2. Ouvrez le fichier `.dmg`
    3. Glissez **ARIG Cipher** dans le dossier **Applications**
    4. Lancez l'application depuis le Launchpad ou Spotlight

    !!! note "Gatekeeper"
        Si macOS bloque l'ouverture, faites **clic droit → Ouvrir** sur l'application la première fois.

=== "Linux (x86_64 / ARM64)"

    Téléchargez le fichier `.deb` correspondant à votre architecture, puis installez-le :

    ```bash
    # x86_64
    sudo dpkg -i arig-cipher-linux-x86_64.deb

    # ARM64
    sudo dpkg -i arig-cipher-linux-arm64.deb
    ```

    L'application est ensuite accessible depuis votre menu d'applications.

=== "Windows (x86_64)"

    1. Téléchargez `arig-cipher-windows-x86_64.zip`
    2. Extrayez le contenu du fichier ZIP (clic droit → Extraire tout)
    3. Ouvrez le dossier extrait
    4. Double-cliquez sur **ARIG Cipher.exe**

    Aucune installation requise — vous pouvez placer le dossier où vous le souhaitez.

---

## Binaire autonome

Pour les utilisateurs qui préfèrent un exécutable sans dossier d'installation :

=== "macOS / Linux"

    1. Téléchargez le binaire (`arig-cipher-macos-arm64-bin` ou `arig-cipher-linux-*-bin`)
    2. Rendez-le exécutable :

    ```bash
    chmod +x arig-cipher-*-bin
    ```

    3. Lancez-le :

    ```bash
    ./arig-cipher-*-bin
    ```

=== "Windows"

    Téléchargez `arig-cipher-windows-x86_64-bin.exe` et double-cliquez dessus.

---

## Premier lancement

Au démarrage, un **splash screen** s'affiche pendant quelques secondes. Cliquez dessus pour passer directement à l'interface principale.

L'application est prête à l'emploi — aucune configuration n'est nécessaire.

[:octicons-arrow-right-24: Apprendre à chiffrer et déchiffrer des fichiers](usage.md)