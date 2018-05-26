package kuliah.rsaftp.helper;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class KeyManager {
    private static final int key_size = RsaConfig.KEY_SIZE;
    private static final KeyPairGenerator generator = initGenerator();
    private static final KeyFactory factory = initFactory();
    private static final Base64.Encoder encoder = Base64.getEncoder();
    private static final Base64.Decoder decoder = Base64.getDecoder();

    private static KeyPairGenerator initGenerator() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(key_size);
            return generator;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static KeyFactory initFactory() {
        try {
            return KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static KeyPair generatePair() {
        return generator.generateKeyPair();
    }

    public static String encodeKey(PrivateKey key) {
        return encoder.encodeToString(new PKCS8EncodedKeySpec(key.getEncoded()).getEncoded());
    }

    public static PrivateKey decodePrivateKey(String encoded) throws InvalidKeySpecException {
        return factory.generatePrivate(new PKCS8EncodedKeySpec(decoder.decode(encoded)));
    }

    public static String encodeKey(PublicKey key) {
        return encoder.encodeToString(new X509EncodedKeySpec(key.getEncoded()).getEncoded());
    }

    public static PublicKey decodePublicKey(String encoded) throws InvalidKeySpecException {
        return factory.generatePublic(new X509EncodedKeySpec(decoder.decode(encoded)));
    }
}
