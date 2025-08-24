package util;

import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;

public class PasswordUtil {

    // 預設使用 SHA-256
    public static String hash(String password) {
        return hashWithAlgorithm(password, "SHA-256");
    }

    // 可選演算法：SHA-256 / MD5
    public static String hashWithAlgorithm(String password, String algorithm) {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            byte[] bytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Hashing error", e);
        }
    }

    // 驗證密碼，會同時嘗試 SHA-256 與 MD5
    public static boolean verify(String password, String hash) {
        String sha256 = hashWithAlgorithm(password, "SHA-256");
        if (sha256.equalsIgnoreCase(hash)) return true;

        String md5 = hashWithAlgorithm(password, "MD5");
        return md5.equalsIgnoreCase(hash);
    }
}