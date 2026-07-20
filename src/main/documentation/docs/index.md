# ARIG Cipher

```
 █████╗ ██████╗ ██╗ ██████╗      ██████╗██╗██████╗ ██╗  ██╗███████╗██████╗
██╔══██╗██╔══██╗██║██╔════╝     ██╔════╝██║██╔══██╗██║  ██║██╔════╝██╔══██╗
███████║██████╔╝██║██║  ███╗    ██║     ██║██████╔╝███████║█████╗  ██████╔╝
██╔══██║██╔══██╗██║██║   ██║    ██║     ██║██╔═══╝ ██╔══██║██╔══╝  ██╔══██╗
██║  ██║██║  ██║██║╚██████╔╝    ╚██████╗██║██║     ██║  ██║███████╗██║  ██║
╚═╝  ╚═╝╚═╝  ╚═╝╚═╝ ╚═════╝      ╚═════╝╚═╝╚═╝     ╚═╝  ╚═╝╚══════╝╚═╝  ╚═╝
```

**Application desktop de chiffrement de fichiers — projet [ARIG Robotique](https://github.com/arig-robotique)**

---

## Présentation

ARIG Cipher est une application desktop développée en **JavaFX + Spring Boot**, conçue pour chiffrer et déchiffrer des fichiers à l'aide de l'algorithme **Lucifer** (réseau de Feistel à 4 rounds). Elle est destinée à protéger des fichiers sensibles tout en offrant une interface graphique soignée et minimaliste, aux couleurs de l'association ARIG Robotique.

### Caractéristiques principales

| Fonctionnalité | Détail |
|---|---|
| Algorithme | Lucifer — réseau de Feistel 4 rounds |
| Interface | JavaFX, thème sombre, couleur principale `#4CAF50` |
| Splash screen | SVG animé logo ARIG, 5 secondes |
| Format de sortie | Extension `.arig` avec en-tête structuré |
| Stack technique | Java 25, Spring Boot 4.1, JavaFX 24 |
| Distribution | Installers natifs (dmg/deb/msi) + binaires autonomes — 4 plateformes |

---

## Navigation rapide

<div class="grid cards" markdown>

-   :material-rocket-launch:{ .lg .middle } **Démarrage rapide**

    ---

    Installez les prérequis, clonez le projet et lancez l'application en quelques minutes.

    [:octicons-arrow-right-24: Getting started](guide/getting-started.md)

-   :material-file-lock:{ .lg .middle } **Guide d'utilisation**

    ---

    Apprenez à chiffrer et déchiffrer des fichiers, comprenez le format `.arig`.

    [:octicons-arrow-right-24: Utilisation](guide/usage.md)

-   :material-cogs:{ .lg .middle } **Architecture**

    ---

    Intégration Spring Boot + JavaFX, cycle de vie de l'application, structure des packages.

    [:octicons-arrow-right-24: Architecture](technical/architecture.md)

-   :material-lock-check:{ .lg .middle } **Algorithme Lucifer**

    ---

    Description du réseau de Feistel, format de l'en-tête `.arig`, limitations connues.

    [:octicons-arrow-right-24: Cipher](technical/cipher.md)

-   :material-package-variant:{ .lg .middle } **Build & Distribution**

    ---

    Installers jpackage, binaires natifs Liberica NIK, CI/CD GitHub Actions.

    [:octicons-arrow-right-24: Native build](technical/native-build.md)

</div>

---

## À propos

Ce projet est développé dans le cadre de l'association **ARIG Robotique**, passionnée de robotique compétitive et de développement logiciel embarqué. ARIG Cipher illustre l'implémentation d'un algorithme de chiffrement historique dans un contexte moderne (Java 25, Liberica NIK, build natif multi-plateforme).

!!! note "Contexte historique"
    L'algorithme **Lucifer** est l'ancêtre direct du standard **DES** (Data Encryption Standard). Conçu par IBM dans les années 1970, il repose sur un réseau de Feistel et a posé les bases de la cryptographie symétrique moderne. L'implémentation utilisée ici est une version éducative simplifiée.
