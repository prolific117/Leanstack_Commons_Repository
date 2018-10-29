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


import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;


public class UtilityObject {
    
    private final static Gson gson = new Gson ();
    
    public static Boolean isNull (Object o) {
        return o == null;
    }
    
    public static Boolean isNotEmpty (String o) {
        return isNull(o) || o.length() == 0;
    }
    
    public static String format (String source, Object...args) {
        return String.format (source, args);
    }
    
    public List<String> validate (Object validatable) {
        ArrayList<String> messages = new ArrayList<> ();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<Object>> violations = validator.validate(validatable);
        if (violations != null) {
            for (ConstraintViolation<Object> violation : violations) {
                messages.add(violation.getMessage());
            }
        }
        return messages;
    }
    
    public String toJson(Object o){
        return gson.toJson(o);
    }
    
    public static String getIpFromRequest (HttpServletRequest request) {
        String address = request.getHeader("X-Forwarded-For");
        address = address == null || address.length() == 0 ? request.getHeader("X-FORWARDED-FOR") : address;
        address = address == null || address.length() == 0 ? request.getHeader("x-forwarded-for") : address;
        address = address == null || address.length() == 0 ? request.getRemoteAddr() : address;
        return address;
    }
    
}

