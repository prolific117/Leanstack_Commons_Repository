/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proxy.leanstack.commons.base;

/**
 *
 * @author prolific
 */


import com.proxy.leanstack.commons.util.EncryptUtils;


public class CLI {
    
    public static void main (String[] args) {
        String value = args[0];
        String encrypted = EncryptUtils.encrpt(ConfigurationManager.ENC_KEY, value);
        System.out.println(encrypted);
    }
}

