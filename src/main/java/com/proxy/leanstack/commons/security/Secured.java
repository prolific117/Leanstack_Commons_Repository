/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proxy.leanstack.commons.security;

/**
 *
 * @author prolific
 */

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.ws.rs.NameBinding;

@NameBinding
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Secured {
    String[] permissions () default {};
    boolean optional () default false;
    String[] signatureFields () default {};
    boolean signed () default false;
    SignatureMethod signatureMethod () default SignatureMethod.SHA_256;
}
