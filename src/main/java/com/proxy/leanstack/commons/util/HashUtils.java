/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proxy.leanstack.commons.util;

/**
 *
 * @author prolific
 */


import com.proxy.leanstack.commons.security.SignatureMethod;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;


public class HashUtils {
    
    public static boolean checkSignature (SignatureMethod method, String signature, String data, String key) {
        if (signature == null || signature.isEmpty() || data == null || data.isEmpty() || method == null) {
            return false;
        }
        String matched = null;
        switch (method) {
            case SHA_1:
                matched = calculateHmac(key, data, "HmacSHA1");
                break;
            case SHA_256:
                matched = calculateHmac(key, data, "HmacSHA256");
                break;
            case SHA_384:
                matched = calculateHmac(key, data, "HmacSHA384");
                break;
            case SHA_512:
                matched = calculateHmac(key, data, "HmacSHA512");
                break;
        }
        return Objects.equals(matched, signature);
    }
    
    private static String toHexString(byte[] bytes) {
        Formatter formatter = new Formatter();
        for (byte b : bytes) {
                formatter.format("%02x", b);
        }
        return formatter.toString();
    }

    public static String calculateHmac(String key, String data, String algorithm) {
        try {
            SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), algorithm);
            Mac mac = Mac.getInstance(algorithm);
            mac.init(signingKey);
            return toHexString(mac.doFinal(data.getBytes()));
        } catch (NoSuchAlgorithmException | InvalidKeyException ex) {
            Logger.getLogger(HashUtils.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public static void main(String[] params) {
        HashUtils h = new HashUtils();
        
        System.out.println(calculateHmac("SIGN@UBAMB@ISW", "1234567890012970369405860", "HmacSHA256"));
        
    }
}

