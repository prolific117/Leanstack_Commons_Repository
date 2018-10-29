/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proxy.leanstack.commons.base;

import java.lang.ref.WeakReference;
import javax.enterprise.inject.spi.InjectionPoint;

/**
 *
 * @author prolific
 */

public class ConfigurationReference <T> {
    
    private WeakReference<T> reference;
    
    private WeakReference<InjectionPoint> ip;

    public ConfigurationReference(T value, InjectionPoint ip) {
        reference = new WeakReference<>(value);
        this.ip = new WeakReference<>(ip);
    }

    public WeakReference<T> getReference() {
        return reference;
    }

    public void setReference(WeakReference<T> reference) {
        this.reference = reference;
    }

    public WeakReference<InjectionPoint> getIp() {
        return ip;
    }

    public void setIp(WeakReference<InjectionPoint> ip) {
        this.ip = ip;
    }
    
    public Boolean isEmpty () {
        return reference.get() == null;
    }
    
    public T get () {
        return reference.get();
    }
    
}
