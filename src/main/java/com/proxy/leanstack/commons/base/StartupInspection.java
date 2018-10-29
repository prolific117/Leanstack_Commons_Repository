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

import com.proxy.leanstack.commons.annotations.Property;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;


@Startup
@Singleton
public class StartupInspection {

    private static final Logger LOG = Logger.getLogger(StartupInspection.class.getName());
    
    @Inject
    @Property (defaultValue = "false")
    private AtomicReference<Boolean> commonsOptionalSignature;
    
    @PostConstruct
    public void init () {
        if (commonsOptionalSignature.get()) {
            LOG.log(Level.WARNING, "commonsOptionalSignature set to true. This is unadvisable. Remove this property or set to false as soon as possible to keep endpoints fully secure");
        }
    }
}
