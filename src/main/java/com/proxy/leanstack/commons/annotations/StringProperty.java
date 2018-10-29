/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proxy.leanstack.commons.annotations;

/**
 *
 * @author prolific
 */

import com.proxy.leanstack.commons.base.ConfigurationManager;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;


@Qualifier
@Retention(RUNTIME)
@Target(
{
    METHOD, FIELD, PARAMETER, TYPE
})
public @interface StringProperty
{
    @Nonbinding public String value() default "";
    @Nonbinding public boolean encrypted() default false;
    @Nonbinding public String defaultValue() default ConfigurationManager.DEFAULT_VALUE;
}
