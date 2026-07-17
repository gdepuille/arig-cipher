package org.arig.lucifer.fx;

import org.arig.lucifer.crypt.Lucifer;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class CipherService {

    public static String encrypt(String srcPath, String destDir) throws Exception {
        File srcFile = new File(srcPath);
        String originalName = srcFile.getName();

        String baseName = originalName.contains(".")
                ? originalName.substring(0, originalName.lastIndexOf('.'))
                : originalName;
        String destPath = destDir + File.separator + baseName + ".arig";

        // Build temp file: [4-byte name length][name bytes][original content]
        Path tempPath = Files.createTempFile("arig_enc_", ".tmp");
        try {
            try (DataOutputStream out = new DataOutputStream(
                    new BufferedOutputStream(new FileOutputStream(tempPath.toFile())))) {
                byte[] nameBytes = originalName.getBytes(StandardCharsets.UTF_8);
                out.writeInt(nameBytes.length);
                out.write(nameBytes);
                Files.copy(srcFile.toPath(), out);
            }

            Lucifer lucifer = new Lucifer(tempPath.toString(), destPath, true);
            if (!Boolean.TRUE.equals(lucifer.getCryptOK())) {
                throw new IOException("Impossible d'ouvrir les fichiers source ou destination.");
            }
            lucifer.start();

            if (!new File(destPath).exists()) {
                throw new IOException("Le fichier de sortie n'a pas été créé.");
            }
        } finally {
            Files.deleteIfExists(tempPath);
        }

        return destPath;
    }

    public static String decrypt(String srcPath, String destDir) throws Exception {
        Path tempPath = Files.createTempFile("arig_dec_", ".tmp");
        try {
            Lucifer lucifer = new Lucifer(srcPath, tempPath.toString(), false);
            if (!Boolean.TRUE.equals(lucifer.getCryptOK())) {
                throw new IOException("Impossible d'ouvrir les fichiers source ou destination.");
            }
            lucifer.start();

            // Read header then restore original file
            try (DataInputStream in = new DataInputStream(
                    new BufferedInputStream(new FileInputStream(tempPath.toFile())))) {

                int nameLength = in.readInt();
                if (nameLength <= 0 || nameLength > 4096) {
                    throw new IOException("Format invalide : ce fichier n'a pas été chiffré avec ARIG Cipher.");
                }

                byte[] nameBytes = in.readNBytes(nameLength);
                String originalName = new String(nameBytes, StandardCharsets.UTF_8);

                if (originalName.contains(File.separator) || originalName.contains("/") || originalName.contains("\\")) {
                    throw new IOException("Nom de fichier invalide dans l'en-tête.");
                }

                String destPath = destDir + File.separator + originalName;
                try (OutputStream out = new BufferedOutputStream(new FileOutputStream(destPath))) {
                    in.transferTo(out);
                }

                return destPath;
            }
        } finally {
            Files.deleteIfExists(tempPath);
        }
    }
}
