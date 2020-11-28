package com.cocode.client.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;

public class KeyUtil {
        private static String iv;
        private static Key keySpec;
        private final static String key = "abldbjwkc2eg01eh";

        static {
            iv = key.substring(0, 16);
            byte[] keyBytes = new byte[16];
            byte[] b = null;

            try {
                b = key.getBytes("UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }

            int len = b.length;
            if (len > keyBytes.length) {
                len = keyBytes.length;
            }

            System.arraycopy(b, 0, keyBytes, 0, len);
            SecretKeySpec s = new SecretKeySpec(keyBytes, "AES");

            keySpec = s;
        }

        public static String encrypt(String str) {
            try {
                Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
                c.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(iv.getBytes()));
                byte[] encrypted = c.doFinal(str.getBytes("UTF-8"));
                String enStr = new String(Base64.getEncoder().encode(encrypted));
                return enStr;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        public static String decrypt(String str) {
            try {
                Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
                c.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv.getBytes()));
                byte[] byteStr = Base64.getDecoder().decode(str.getBytes());
                return new String(c.doFinal(byteStr), "UTF-8");
            } catch (Exception e) {
                return null;
            }
        }
}
