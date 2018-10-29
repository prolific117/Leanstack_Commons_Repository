/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proxy.leanstack.commons.validation;

/**
 *
 * @author prolific
 */
import com.proxy.leanstack.commons.base.ConfigurationManager;
import com.proxy.leanstack.commons.util.EncryptUtils;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.ws.rs.NameBinding;


@NameBinding
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(value = RetentionPolicy.RUNTIME)
@Repeatable(Validated.class)
public @interface Validatable {
    String field ();
    String regex () default ConfigurationManager.DEFAULT_VALUE;
    String fieldName ();
    int minLength () default 0;
    int maxLength () default Integer.MAX_VALUE;
}

