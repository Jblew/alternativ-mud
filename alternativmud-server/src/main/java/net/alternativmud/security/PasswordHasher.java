/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.security;

import com.google.common.base.Charsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jblew
 */
public class PasswordHasher {
    private static MessageDigest md = null;
    
    public static boolean algorithmExists() {
        try {
            MessageDigest.getInstance("SHA1");
            return true;
        } catch (NoSuchAlgorithmException ex) {
            return false;
        }
    }
    
    public static void init() throws NoSuchAlgorithmException {
        md = MessageDigest.getInstance("SHA1");
    }
    
    private static String convertToHex(byte[] data) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                if ((0 <= halfbyte) && (halfbyte <= 9)) {
                    buf.append((char) ('0' + halfbyte));
                } else {
                    buf.append((char) ('a' + (halfbyte - 10)));
                }
                halfbyte = data[i] & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }

    private static String sha1(String text) {
        if(md == null) {
            try {
                md = MessageDigest.getInstance("SHA1");
            } catch (NoSuchAlgorithmException ex) {
                throw new RuntimeException("Could not generate hash. MessageDigest is null. Algorithm SHA1 does not exist.");
            }
        }
        md.update(text.getBytes(Charsets.UTF_8), 0, text.length());
        byte [] sha1hash = md.digest();
        return convertToHex(sha1hash);
    }

    public static String generateHash(String in) {
        return sha1(in);
    }
}
