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
import com.proxy.leanstack.commons.util.EncryptUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Observable;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;


@Startup
@Singleton

public class ConfigurationManager extends Observable
{
    
    public static final String ENC_KEY = "z5HXm$<u@#*Y2(Z=";
    
    public static final String DEFAULT_VALUE = "]TQ+hXv2`LK^DyNPc`>#z7BL(,%7d\"{XeE%pMcc.BPuS/f%@xnTgDm";

    private Properties properties;
    
    private static final Logger LOG = Logger.getLogger(ConfigurationManager.class.getName());
    
    @Inject
    private ConfigurationLocationProvider provider;
    
    private final ScheduledExecutorService es = Executors.newSingleThreadScheduledExecutor();
    
    private Long lastModifiedTime = null;

    public static final Map<String, List<ConfigurationReference<AtomicReference<String>>>> strings = new ConcurrentHashMap<>();
    public static final Map<String, List<ConfigurationReference<AtomicReference<Boolean>>>> booleans = new ConcurrentHashMap<>();
    public static final Map<String, List<ConfigurationReference<AtomicReference<Float>>>> floats = new ConcurrentHashMap<>();
    public static final Map<String, List<ConfigurationReference<AtomicReference<Integer>>>> integers = new ConcurrentHashMap<>();
    public static final Map<String, List<ConfigurationReference<AtomicReference<BigDecimal>>>> bigs = new ConcurrentHashMap<>();
    public static final Map<String, List<ConfigurationReference<AtomicReference<Double>>>> doubles = new ConcurrentHashMap<>();
    
    @PostConstruct
    void init()
    {
        loadProperties();
        String dontWatch = System.getProperty("CommonsConfigWatcherDeactivate");
        if (dontWatch != null && "true".equals(dontWatch.trim())) {
            return;
        }
        es.scheduleAtFixedRate(() -> {
            if (!Objects.equals(lastModifiedTime, getLastModified(provider.getSource()))) {
                LOG.log(Level.INFO, "Configuration change detected");
                loadProperties();
                update();
            }
        }, 5, 5, TimeUnit.SECONDS);
    }
    
    @PreDestroy
    public void destroy () {
        es.shutdown();
    }
    
    private void loadProperties () {
        try
        {
            properties = new Properties();
            String source = provider.getSource();
             LOG.log(Level.SEVERE, source);
            if (source == null || !new File(source).exists()) {
                LOG.log(Level.SEVERE, "Configuration Manager either not setup or File not exists for {0}", Optional.ofNullable(source).orElse("Null"));
                return;
            }
            lastModifiedTime = getLastModified(source);
            if (source != null) {
                LOG.log(Level.INFO, "Loading proxy configurations from: {0}", source);
                InputStream stream = new FileInputStream(source);
                properties.load(stream);
                LOG.log(Level.INFO, "Configuration Manager initialised");
            } else {
                LOG.severe("Configuration Manager did not find a valid source");
            }
        } catch (Exception ex)
        {
            LOG.log(Level.SEVERE, "Error initialising configuration manager ", ex);
        }
    }
    
    private long getLastModified (String file) {
        return new File(file).lastModified();
    }
    
    public String getProperty(String key)
    {
        String val = properties.getProperty(key);
        if (val != null) {
            val = val.trim();
        }
        return val;
    }
    
    /**
     * This phase allows you reference another property within a property
     * For example, assume there is a property API_HOST
     * This property can be referenced within another property as GET_USER = ${API_HOST}/users
     * @param isEncrypted
     * @param property
     * @return 
     */
    public String parse (boolean isEncrypted, String property) {
        try {
            if (property == null) {
               return property;
            }
            if (isEncrypted) {
                return EncryptUtils.decrypt(ConfigurationManager.ENC_KEY, property);
            }
            String compiled = new StringBuffer (property).toString();
            Matcher m = Pattern.compile("\\$\\{(\\w*?)\\}").matcher(compiled);
            Map<String, String> vars= new HashMap<>();
            boolean propertyIsParceable = false;
            while (m.find()) {
                vars.put(m.group(1), null);
                propertyIsParceable = true;
            } 
            boolean foundSubKey = false;
            
            if (!propertyIsParceable) {
                return property;
            }
            for (String var : vars.keySet()) {
                String val = getProperty(var);
                if (val != null) {
                    foundSubKey = true;
                    compiled = compiled.replaceAll("\\$\\{"+var+"\\}", parse(isEncrypted, val));
                }
            }
            // Were valid replacements made?
            if (!foundSubKey) {
                return property;
            } else {
                return compiled;
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Could not parse property " + property, e);
        }
        return property;
    }
    
    public void update() {
        LOG.log(Level.INFO, "Refreshing configuration properties");
        Properties property = properties;
        for (Map.Entry<Object, Object> entry : property.entrySet()) {
            if (strings.containsKey((String) entry.getKey())) {
                List<ConfigurationReference<AtomicReference<String>>> refs = strings.get((String) entry.getKey());
                Iterator<ConfigurationReference<AtomicReference<String>>> it = refs.iterator();
                while (it.hasNext()) {
                    ConfigurationReference<AtomicReference<String>> val = it.next();
                    if (!val.isEmpty()) {
                        val.get().set(parse(val.getIp().get() != null ? val.getIp().get().getAnnotated().getAnnotation(Property.class).encrypted() : false, (String) entry.getValue()));
                    } else {
                        it.remove();
                    }
                }
            }
            if (booleans.containsKey((String) entry.getKey())) {
                List<ConfigurationReference<AtomicReference<Boolean>>> refs = booleans.get((String) entry.getKey());
                Iterator<ConfigurationReference<AtomicReference<Boolean>>> it = refs.iterator();
                while (it.hasNext()) {
                    ConfigurationReference<AtomicReference<Boolean>> val = it.next();
                    if (!val.isEmpty()) {
                        String strBool = (String) entry.getValue();
                        val.get().set(Boolean.parseBoolean(strBool));
                    } else {
                        it.remove();
                    }
                }
            }
            if (doubles.containsKey((String) entry.getKey())) {
                List<ConfigurationReference<AtomicReference<Double>>> refs = doubles.get((String) entry.getKey());
                Iterator<ConfigurationReference<AtomicReference<Double>>> it = refs.iterator();
                while (it.hasNext()) {
                    ConfigurationReference<AtomicReference<Double>> val = it.next();
                    if (!val.isEmpty()) {
                        val.get().set(Double.parseDouble((String) entry.getValue()));
                    } else {
                        it.remove();
                    }
                }
            }
            if (floats.containsKey((String) entry.getKey())) {
                List<ConfigurationReference<AtomicReference<Float>>> refs = floats.get((String) entry.getKey());
                Iterator<ConfigurationReference<AtomicReference<Float>>> it = refs.iterator();
                while (it.hasNext()) {
                    ConfigurationReference<AtomicReference<Float>> val = it.next();
                    if (!val.isEmpty()) {
                        val.get().set(Float.parseFloat((String) entry.getValue()));
                    } else {
                        it.remove();
                    }
                }
            }
            if (integers.containsKey((String) entry.getKey())) {
                List<ConfigurationReference<AtomicReference<Integer>>> refs = integers.get((String) entry.getKey());
                Iterator<ConfigurationReference<AtomicReference<Integer>>> it = refs.iterator();
                while (it.hasNext()) {
                    ConfigurationReference<AtomicReference<Integer>> val = it.next();
                    if (!val.isEmpty()) {
                        val.get().set(Integer.parseInt((String) entry.getValue()));
                    } else {
                        it.remove();
                    }
                }
            }
            if (bigs.containsKey((String) entry.getKey())) {
                List<ConfigurationReference<AtomicReference<BigDecimal>>> refs = bigs.get((String) entry.getKey());
                Iterator<ConfigurationReference<AtomicReference<BigDecimal>>> it = refs.iterator();
                while (it.hasNext()) {
                    ConfigurationReference<AtomicReference<BigDecimal>> val = it.next();
                    if (!val.isEmpty()) {
                        val.get().set(new BigDecimal((String) entry.getValue()));
                    } else {
                        it.remove();
                    }
                }
            }
        }
    }
    
}
