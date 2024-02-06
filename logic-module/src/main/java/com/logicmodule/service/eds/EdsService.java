package com.logicmodule.service.eds;


import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.data.util.Pair;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

@Slf4j
public class EdsService {

    public static KeyPair generateKeyPair()
            throws NoSuchAlgorithmException {
        var provider = new BouncyCastleProvider();
        Security.addProvider(provider);
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA",
                provider);
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair();
    }

    public static byte[] signDocument(InputStream documentFile, PrivateKey privateKey)
            throws InvalidKeyException, NoSuchAlgorithmException,
            NoSuchProviderException, SignatureException, IOException {
        Security.addProvider(new BouncyCastleProvider());
        Signature signature = Signature.getInstance("SHA256withRSA", "BC");
        signature.initSign(privateKey);
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = documentFile.read(buffer)) != -1) {
            signature.update(buffer, 0, bytesRead);
        }
        documentFile.close();
        return signature.sign();
    }

    public static byte[] privateKeyToBytes(PrivateKey privateKey) {
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKey.getEncoded());
        return keySpec.getEncoded();
    }

    public static PrivateKey bytesToPrivateKey(byte[] privateKeyBytes)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        return keyFactory.generatePrivate(keySpec);
    }

    public static byte[] publicKeyToBytes(PublicKey publicKey) {
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKey.getEncoded());
        return keySpec.getEncoded();
    }

    public static PublicKey bytesToPublicKey(byte[] publicKeyBytes)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        return keyFactory.generatePublic(keySpec);
    }

    public static byte[] combineDocumentAndSignature(byte[] document, byte[] signature, String username) {
        byte[] metadataBytes = username.getBytes(StandardCharsets.UTF_8);
        byte[] combinedData = new byte[metadataBytes.length + document.length
                + signature.length];
        System.arraycopy(document, 0, combinedData, 0, document.length);
        System.arraycopy(metadataBytes, 0, combinedData, document.length, metadataBytes.length);
        System.arraycopy(signature, 0, combinedData,
                document.length + metadataBytes.length, signature.length);
        return combinedData;
    }

    public static Pair<byte[], byte[]> splitDocumentAndSignature(byte[] combinedData, String username) {
        byte[] metadataBytes = username.getBytes(StandardCharsets.UTF_8);
        int startIndex = -1;
        int j = 0;
        for (int i = 0; i < combinedData.length; i++) {
            if (combinedData[i] == metadataBytes[j]) {
                j++;
                if (j == metadataBytes.length) {
                    startIndex = i - j + 1;
                    break;
                }
            } else {
                j = 0;
            }
        }
        if (startIndex != -1) {
            byte[] document = Arrays.copyOfRange(combinedData, 0, startIndex);
            byte[] signature = Arrays.copyOfRange(combinedData,
                    startIndex + metadataBytes.length, combinedData.length);
            return Pair.of(document, signature);
        } else {
            return null;
        }
    }



    public static boolean verifySignature(byte[] document, byte[] signature, PublicKey publicKey) {
        try {
            Security.addProvider(new BouncyCastleProvider());
            Signature verifier = Signature.getInstance("SHA256withRSA", "BC");
            verifier.initVerify(publicKey);
            verifier.update(document);
            return verifier.verify(signature);
        } catch (Exception e) {

            return false;
        }
    }
}
