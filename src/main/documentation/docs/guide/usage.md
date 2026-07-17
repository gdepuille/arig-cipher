# Guide d'utilisation

Ce guide décrit comment utiliser ARIG Cipher pour chiffrer et déchiffrer vos fichiers, ainsi que le comportement attendu de l'application dans chaque scénario.

---

## Démarrage de l'application

Lancez l'application via la commande Gradle ou en exécutant le binaire natif (si disponible) :

```bash
./gradlew bootRun
```

### Splash screen

À l'ouverture, un **splash screen** animé s'affiche avec le logo ARIG Robotique en SVG animé.

| Action | Comportement |
|---|---|
| Attendre | Fermeture automatique après **5 secondes** avec un effet de fondu |
| Cliquer | Fermeture immédiate du splash screen |

L'interface principale s'ouvre ensuite dans une fenêtre au thème sombre.

---

## Chiffrement d'un fichier

### Étape 1 — Sélectionner le fichier source

Dans l'interface principale, utilisez le bouton **"Chiffrer"** (ou l'action équivalente) pour ouvrir un sélecteur de fichier. Naviguez jusqu'au fichier que vous souhaitez protéger.

!!! tip "Types de fichiers supportés"
    ARIG Cipher peut chiffrer **n'importe quel type de fichier** : documents, images, archives, exécutables, etc. Le chiffrement opère octet par octet, indépendamment du format.

### Étape 2 — Choisir le répertoire de destination

Un sélecteur de dossier vous permet de définir où le fichier chiffré sera créé.

### Étape 3 — Lancer le chiffrement

Confirmez l'opération. ARIG Cipher :

1. Lit le contenu du fichier source
2. Encode le nom du fichier original en UTF-8
3. Construit l'en-tête `.arig` (longueur du nom + nom original)
4. Chiffre le contenu byte par byte avec l'algorithme Lucifer
5. Écrit le fichier de sortie avec l'extension `.arig`

### Résultat

Le fichier produit est nommé `<nom_original>.arig` dans le répertoire de destination.

**Exemple :**

```
rapport_secret.pdf  →  rapport_secret.pdf.arig
```

!!! warning "Fichier source conservé"
    Le fichier source **n'est pas supprimé** après chiffrement. Pensez à le supprimer manuellement si nécessaire.

---

## Déchiffrement d'un fichier

### Étape 1 — Sélectionner le fichier `.arig`

Dans l'interface principale, utilisez le bouton **"Déchiffrer"**. Sélectionnez un fichier portant l'extension `.arig`.

### Détection automatique de l'extension

!!! note "Auto-détection"
    L'application détecte automatiquement si un fichier a l'extension `.arig` pour déterminer l'opération à effectuer. Vous n'avez pas à choisir manuellement entre chiffrement et déchiffrement si ce comportement est activé.

### Étape 2 — Choisir le répertoire de destination

Sélectionnez le dossier où le fichier déchiffré sera restauré.

### Étape 3 — Lancer le déchiffrement

ARIG Cipher :

1. Lit l'en-tête du fichier `.arig` pour extraire le nom original du fichier
2. Déchiffre le contenu avec la même clé Lucifer
3. Restaure le fichier avec son **nom original** dans le répertoire de destination

**Exemple :**

```
rapport_secret.pdf.arig  →  rapport_secret.pdf
```

!!! warning "Clé fixe"
    La clé de chiffrement est **hard-codée** dans l'application (`[b0=1, b1=4, b2=3, b3=2]`). Tout fichier `.arig` créé avec ARIG Cipher peut être déchiffré par n'importe quelle instance de l'application. Ce mécanisme n'est **pas adapté** à une utilisation en environnement hostile avec des exigences de sécurité élevées.

---

## Format du fichier `.arig`

Un fichier `.arig` est structuré de la manière suivante :

```
┌─────────────────────────────────────────────────────────────────┐
│  [4 octets]  Longueur du nom original (int big-endian)          │
├─────────────────────────────────────────────────────────────────┤
│  [N octets]  Nom original du fichier encodé en UTF-8            │
├─────────────────────────────────────────────────────────────────┤
│  [suite]     Contenu chiffré (Lucifer, byte par byte)           │
└─────────────────────────────────────────────────────────────────┘
```

**Exemple pour `photo.jpg` :**

| Offset | Longueur | Valeur | Description |
|---|---|---|---|
| 0 | 4 octets | `0x00 0x00 0x00 0x09` | Longueur du nom = 9 |
| 4 | 9 octets | `photo.jpg` (UTF-8) | Nom original |
| 13 | variable | `...` | Contenu de `photo.jpg` chiffré |

---

## Comportements notables

### Fichier déjà chiffré

Si vous tentez de chiffrer un fichier `.arig`, l'application le traitera comme n'importe quel autre fichier binaire. Le nom du fichier `.arig` sera stocké dans l'en-tête du nouveau `.arig`.

### Noms de fichiers avec caractères spéciaux

Les noms de fichiers sont encodés en **UTF-8**, ce qui garantit la compatibilité avec les caractères accentués, les idéogrammes et tout caractère Unicode valide.

### Fichiers volumineux

Le traitement s'effectue en flux (`InputStream` / `OutputStream`). Les fichiers volumineux ne sont pas chargés entièrement en mémoire, ce qui limite le risque d'`OutOfMemoryError`.

!!! warning "Performance sur gros fichiers"
    L'algorithme Lucifer traite les données **octet par octet**. Pour des fichiers de plusieurs centaines de mégaoctets, le temps de traitement peut être perceptible. Aucune barre de progression n'est affichée dans la version actuelle.
