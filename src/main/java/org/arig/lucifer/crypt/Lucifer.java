/*
 * Lucifer.java
 *
 * Created on 4 d�cembre 2006, 14:07
 *
 */

package org.arig.lucifer.crypt;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.*;

@Slf4j
public class Lucifer {
    private final Boolean typeCrypt;

    @Getter
    private final Boolean cryptOK;

    private int b0, b1, b2, b3;

    private DataInputStream source;
    private DataOutputStream destination;

    public Lucifer(String srcFile, String destFile, Boolean crypt) {
        log.info("Instance de la librairie Lucifer.");
        typeCrypt = crypt;
        if (cryptOK = lectureKey()) {
            try {
                source = new DataInputStream(new BufferedInputStream(new FileInputStream(srcFile)));
            } catch (Exception e) {
                log.error("Erreur d'ouverture du fichier source.");
            }

            try {
                destination = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(destFile)));
            } catch (Exception e) {
                log.error("Erreur d'ouverture du fichier destination.");
            }
        }
    }

    public void start() {
        if (typeCrypt) {
            log.info("Cryptage ...\t\t");
            crypter();
        } else {
            log.info("Decryptage ...\t\t");
            decrypter();
        }
        log.info(" -> [OK]");
    }

    public int crypter(int val) {
        int result, MSB, LSB, temp;
        LSB = val & 0xF;
        MSB = val >> 4;
        for (int cpt = 0; cpt < 4; cpt++) {
            temp = LSB;
            LSB = permut(LSB) ^ MSB; // D1 = D0' xor G0
            MSB = temp; // G1 = D0
        }
        result = (MSB << 4) + LSB;
        return result;
    }

    public int decrypter(int val) {
        int MSB, LSB, temp;
        LSB = val & 0xF;
        MSB = val >> 4;
        for (int cpt = 0; cpt < 4; cpt++) {
            temp = LSB; // Sauvegarde de D3
            LSB = MSB; // D2 = G3
            MSB = permut(LSB) ^ temp; // G2 = D2' xor D3
        }
        return (MSB << 4) + LSB;
    }

    private Boolean lectureKey() {
        int erreur = 0;
        int[] cle = new int[4];
        //try{
        //BufferedReader key = new BufferedReader(new FileReader("key.txt"));
            /*b0 = key.read() - 48;key.read();
            b1 = key.read() - 48;key.read();
            b2 = key.read() - 48;key.read();
            b3 = key.read() - 48;
            key.close();*/

        b0 = 1;
        b1 = 4;
        b2 = 3;
        b3 = 2;

        cle[0] = b0;
        cle[1] = b1;
        cle[2] = b2;
        cle[3] = b3;
        for (int cpt = 0; (cpt < 4) && (erreur == 0); cpt++) {
            if ((cle[cpt] < 1) || (cle[cpt] > 4)) {
                erreur = 1;
                break;
            }
        }

        for (int i = 0; (i < 4) && (erreur == 0); i++) {
            for (int j = 0; (j < 4) && (erreur == 0); j++) {
                if ((cle[i] == cle[j]) && (i != j)) {
                    erreur = 1;
                    break;
                }
            }
        }
 
        /*} catch (IOException e) {
            log.info(e);
            erreur = 1;
        }*/

        if (erreur == 1)
            log.info("La clé est incorrecte.");
        return (erreur == 0);
    }

    private void crypter() {
        int carac;
        try {
            while ((carac = source.read()) != -1) {
                carac = crypter(carac);
                destination.write(carac);
            }
            source.close();
            destination.flush();
            destination.close();
        } catch (Exception e) {
            log.error("Erreur de chiffrement", e);
        }
    }

    private void decrypter() {
        int carac;
        try {
            while ((carac = source.read()) != -1) {
                carac = decrypter(carac);
                destination.write(carac);
            }
            source.close();
            destination.flush();
            destination.close();
        } catch (Exception e) {
            log.error("Erreur de déchiffrement", e);
        }
    }

    private int permut(int val) {
        int a0, a1, a2, a3;
        a0 = (val & 1);
        a1 = (val & 2) >> 1;
        a2 = (val & 4) >> 2;
        a3 = (val & 8) >> 3;
        return (a0 << (b0 - 1)) + (a1 << (b1 - 1)) + (a2 << (b2 - 1)) + (a3 << (b3 - 1));
    }
}
