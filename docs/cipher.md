# Algorithme Lucifer

Cette page décrit l'algorithme de chiffrement utilisé par ARIG Cipher : une implémentation du chiffre **Lucifer** sous forme d'un réseau de Feistel à 4 rounds, ainsi que le format binaire des fichiers `.arig`.

---

## Contexte historique

**Lucifer** est un algorithme de chiffrement par blocs développé par **IBM** dans les années 1970, principalement par Horst Feistel. Il est l'ancêtre direct du **DES** (Data Encryption Standard), adopté par le NIST en 1977.

Le principe fondateur — le **réseau de Feistel** — est toujours utilisé dans des algorithmes modernes (Blowfish, Twofish, Camellia). Sa propriété clé : la même structure permet de chiffrer et de déchiffrer, en inversant simplement l'ordre des sous-clés.

!!! note "Implémentation éducative"
    L'implémentation dans ARIG Cipher est une version **simplifiée à des fins éducatives**. Elle opère sur des octets individuels (et non des blocs de 64 bits comme le Lucifer original), avec une clé fixe et 4 rounds. Elle ne doit pas être utilisée pour protéger des données sensibles dans un contexte de sécurité réel.

---

## Réseau de Feistel

### Principe général

Un réseau de Feistel divise le bloc de données en deux moitiés (gauche **L** et droite **R**), puis applique itérativement une fonction de round :

```
Round i :
  L[i+1] = R[i]
  R[i+1] = L[i] XOR F(R[i], K[i])
```

Où `F` est une fonction non-linéaire (la "boîte de confusion") et `K[i]` est la sous-clé du round `i`.

### Propriété de déchiffrement

Le déchiffrement utilise la même structure, en inversant l'ordre des sous-clés :

```
Déchiffrement, round i :
  R[i] = L[i+1]
  L[i] = R[i+1] XOR F(L[i+1], K[n-1-i])
```

Cette symétrie est une propriété remarquable du réseau de Feistel.

---

## Implémentation dans ARIG Cipher

### Paramètres

| Paramètre | Valeur |
|---|---|
| Taille du bloc | 1 octet (8 bits), divisé en deux nibbles de 4 bits |
| Nombre de rounds | 4 |
| Taille de la clé | 4 sous-clés d'un nibble chacune |
| Clé hard-codée | `[b0=1, b1=4, b2=3, b3=2]` |

### Structure d'un round

Chaque octet est traité comme un bloc de 8 bits, découpé en deux **nibbles** (demi-octets) de 4 bits :

```
Octet : [  nibble_gauche (bits 7-4)  |  nibble_droit (bits 3-0)  ]
```

Un round Feistel appliqué sur ce bloc :

```java
// Pseudo-code d'un round
int L = (byte >> 4) & 0x0F;   // nibble haut
int R = byte & 0x0F;           // nibble bas

int newL = R;
int newR = L ^ F(R, subKey);   // XOR avec F(R, sous-clé)

byte_sortie = (newL << 4) | newR;
```

### Fonction F

La fonction `F(nibble, subKey)` est une **substitution non-linéaire** combinée avec la sous-clé :

```java
int F(int nibble, int subKey) {
    return (nibble ^ subKey) & 0x0F;  // XOR et masque 4 bits
}
```

!!! note "Linéarité de F"
    Dans cette implémentation simplifiée, `F` est linéaire (XOR pur). Dans Lucifer original et DES, `F` intègre des S-boxes (boîtes de substitution non-linéaires) pour la résistance à la cryptanalyse différentielle et linéaire.

### Chiffrement complet (4 rounds)

```java
public byte encrypt(byte input) {
    int[] subKeys = {1, 4, 3, 2}; // b0, b1, b2, b3
    int block = input & 0xFF;

    for (int round = 0; round < 4; round++) {
        int L = (block >> 4) & 0x0F;
        int R = block & 0x0F;
        int newL = R;
        int newR = L ^ F(R, subKeys[round]);
        block = (newL << 4) | newR;
    }

    return (byte) block;
}
```

### Déchiffrement complet (ordre des sous-clés inversé)

```java
public byte decrypt(byte input) {
    int[] subKeys = {2, 3, 4, 1}; // ordre inversé : b3, b2, b1, b0
    int block = input & 0xFF;

    for (int round = 0; round < 4; round++) {
        int L = (block >> 4) & 0x0F;
        int R = block & 0x0F;
        int newR = L;         // inverse : newR = L de l'étape précédente
        int newL = R ^ F(L, subKeys[round]);
        block = (newL << 4) | newR;
    }

    return (byte) block;
}
```

!!! tip "Symétrie Feistel"
    Le déchiffrement est structurellement identique au chiffrement — seul l'ordre des sous-clés change. Cette propriété simplifie considérablement l'implémentation matérielle et logicielle.

---

## Format du fichier `.arig`

### Spécification de l'en-tête

```
Offset 0         4           4+N
       │         │           │
       ▼         ▼           ▼
       ┌─────────┬───────────┬──────────────────────────────┐
       │  4 oct  │  N octets │  variable                    │
       │ (int BE)│  (UTF-8)  │  (contenu chiffré)           │
       ├─────────┼───────────┼──────────────────────────────┤
       │   N     │  nom      │  Lucifer(octet[0]),          │
       │ (Big-   │ original  │  Lucifer(octet[1]),          │
       │ Endian) │  fichier  │  ...                         │
       └─────────┴───────────┴──────────────────────────────┘
```

| Champ | Taille | Encodage | Description |
|---|---|---|---|
| `name_length` | 4 octets | `int` big-endian | Nombre d'octets du nom de fichier encodé en UTF-8 |
| `file_name` | `name_length` octets | UTF-8 | Nom original du fichier source (sans chemin) |
| `encrypted_content` | variable | Lucifer byte-by-byte | Contenu du fichier source chiffré |

### Exemple concret

Pour chiffrer `photo_vacances.jpg` (18 caractères ASCII = 18 octets UTF-8) :

```
Octets 0-3  : 0x00 0x00 0x00 0x12       → name_length = 18
Octets 4-21 : 70 68 6F 74 6F 5F 76 61   → "photo_va"
              63 61 6E 63 65 73 2E 6A   → "cances.j"
              70 67                     → "pg"
Octets 22+  : [contenu de photo_vacances.jpg chiffré octet par octet]
```

### Lecture de l'en-tête (pseudocode)

```java
DataInputStream dis = new DataInputStream(new FileInputStream(file));

// 1. Lire la longueur du nom
int nameLength = dis.readInt(); // big-endian natif en Java

// 2. Lire le nom original
byte[] nameBytes = new byte[nameLength];
dis.readFully(nameBytes);
String originalName = new String(nameBytes, StandardCharsets.UTF_8);

// 3. Lire et déchiffrer le contenu
// (le reste du flux est le contenu chiffré)
```

---

## Clé de chiffrement

La clé est composée de 4 sous-clés (nibbles) hard-codées :

| Sous-clé | Valeur (décimal) | Valeur (hex) | Utilisation |
|---|---|---|---|
| `b0` | 1 | `0x1` | Round 1 (chiffrement) |
| `b1` | 4 | `0x4` | Round 2 (chiffrement) |
| `b2` | 3 | `0x3` | Round 3 (chiffrement) |
| `b3` | 2 | `0x2` | Round 4 (chiffrement) |

Pour le déchiffrement, l'ordre est inversé : `b3, b2, b1, b0` = `[2, 3, 4, 1]`.

!!! warning "Clé publique et fixe"
    La clé est identique dans toutes les instances de l'application et est visible dans le code source. Cela signifie que **tout fichier `.arig` peut être déchiffré par n'importe qui ayant accès à l'application ou au code source**. Ce design correspond à un usage interne/pédagogique, pas à une protection cryptographique sérieuse.

---

## Limitations connues

### Sécurité

| Limitation | Impact |
|---|---|
| Clé hard-codée et publique | Protection nulle contre un attaquant ayant accès à l'application |
| 4 rounds seulement | Résistance insuffisante à la cryptanalyse différentielle |
| Pas de S-boxes non-linéaires | Fonction F linéaire, vulnérable à la cryptanalyse linéaire |
| Traitement octet par octet | Pas de diffusion inter-octets (l'octet 0 ne dépend pas de l'octet 1) |
| Pas d'IV / mode CBC | Deux octets identiques en clair produisent le même chiffré |

### Performance

| Limitation | Impact |
|---|---|
| Traitement séquentiel byte par byte | Lent sur les gros fichiers (pas de vectorisation SIMD) |
| Mono-thread | Pas de parallélisation du chiffrement |

### Compatibilité

| Limitation | Impact |
|---|---|
| Nom de fichier UTF-8 | Les noms contenant des octets invalides UTF-8 sont rejetés |
| `int` 32 bits pour `name_length` | Noms de fichiers > 2 Go en UTF-8 non supportés (cas théorique) |

!!! warning "Usage recommandé"
    ARIG Cipher est conçu pour un usage **éducatif et interne**. Pour protéger des données sensibles, utilisez des solutions éprouvées comme **AES-256-GCM** (via la JCA Java ou des bibliothèques comme Tink ou Bouncy Castle).
