package kuliah.rsaftp.helper;

import com.google.common.primitives.Bytes;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.ByteArrayOutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class BytesTransform {
    private static final int encrypt_block = RsaConfig.ENCRYPT_BLOCK;
    private static final int decrypt_block = RsaConfig.DECRYPT_BLOCK;
    private static int buffer_size = 1024;

    public static byte[] compressAndEncrypt(byte[] data, PublicKey key) {
        return encrypt(compress(data), key);
    }

    public static byte[] decryptAndDecompress(byte[] data, PrivateKey key) {
        return decompress(decrypt(data, key));
    }

    private static byte[] compress(byte[] data) {
        Deflater deflater = new Deflater();
        deflater.setLevel(Deflater.BEST_COMPRESSION);
        deflater.setInput(data);
        deflater.finish();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[buffer_size];
        while (!deflater.finished()) {
            int count = deflater.deflate(buffer);
            outputStream.write(buffer, 0, count);
        }
        return outputStream.toByteArray();
    }

    private static byte[] decompress(byte[] data) {
        try {
            Inflater inflater = new Inflater();
            inflater.setInput(data);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
            byte[] buffer = new byte[buffer_size];
            while (!inflater.finished()) {
                int count = inflater.inflate(buffer);
                outputStream.write(buffer, 0, count);
            }
            return outputStream.toByteArray();
        } catch (DataFormatException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] encrypt(byte[] data, PublicKey key) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return executeCipher(cipher, data, encrypt_block);
        } catch (IllegalBlockSizeException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] decrypt(byte[] data, PrivateKey key) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, key);
            return executeCipher(cipher, data, decrypt_block);
        } catch (NoSuchAlgorithmException | IllegalBlockSizeException | BadPaddingException | NoSuchPaddingException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] executeCipher(Cipher cipher, byte[] original, int blockSize) throws BadPaddingException, IllegalBlockSizeException {
        List<Byte> result = new LinkedList<>();
        for (int i = 0; i < original.length; i += blockSize) {
            int size = Math.min(blockSize, original.length - i);
            byte[] block = splitBlock(original, i, size);
            byte[] encryptedBlock = cipher.doFinal(block);
            result.addAll(Bytes.asList(encryptedBlock));
        }
        return Bytes.toArray(result);
    }

    private static byte[] splitBlock(byte[] original, int startIdx, int blockSize) {
        return Arrays.copyOfRange(original, startIdx, startIdx + blockSize);
    }
}
