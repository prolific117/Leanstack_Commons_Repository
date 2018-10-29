/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proxy.leanstack.commons.security;

import org.mindrot.jbcrypt.BCrypt;

/**
 *
 * @author prolific
 */
public class EncryptionTool {
    
    public static String hashPassword(String password){
        
        String pass = BCrypt.hashpw(password, BCrypt.gensalt());
        return pass;
    }
    
    public static boolean comparePassword(String password, String hash){
        Boolean status = BCrypt.checkpw(password, prepend(hash));
        return status;
    }
    
    private static String prepend(String hash){
        return "$2a$" + hash.substring(4);
    }
    
}
