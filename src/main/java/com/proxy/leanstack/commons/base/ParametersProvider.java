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


import com.proxy.leanstack.commons.annotations.BooleanProperty;
import com.proxy.leanstack.commons.annotations.DoubleProperty;
import com.proxy.leanstack.commons.annotations.IntegerProperty;
import com.proxy.leanstack.commons.annotations.Property;
import com.proxy.leanstack.commons.annotations.StringProperty;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;


@Dependent
public class ParametersProvider
{
    @Inject
    private ConfigurationManager config;
    
    private static final Logger LOG = Logger.getLogger(ParametersProvider.class.getName());
    
    private static Map<String, List<ConfigurationReference<AtomicReference<String>>>> strings = null;
    private static Map<String, List<ConfigurationReference<AtomicReference<Boolean>>>> booleans= null;
    private static Map<String, List<ConfigurationReference<AtomicReference<Float>>>> floats= null;
    private static Map<String, List<ConfigurationReference<AtomicReference<Integer>>>> integers= null;
    private static Map<String, List<ConfigurationReference<AtomicReference<BigDecimal>>>> bigs= null;
    private static Map<String, List<ConfigurationReference<AtomicReference<Double>>>> doubles= null;
    
    @PostConstruct
    public void init () {
        strings = ConfigurationManager.strings;
        booleans = ConfigurationManager.booleans;
        floats = ConfigurationManager.floats;
        integers = ConfigurationManager.integers;
        bigs = ConfigurationManager.bigs;
        doubles = ConfigurationManager.doubles;
    }
     
    @Produces 
    @Property
    public BigDecimal getBigDecimal (InjectionPoint ip)
    {
        Double value = getDoubleParameter(ip);
        if (value == null) {
            return null;
        }
        return new BigDecimal(getDoubleParameter(ip));
    }

    @Produces 
    @Property
    public Float getFloat (InjectionPoint ip)
    {
        Double value = getDoubleParameter(ip);
        if (value == null) {
            return null;
        }
        return new Float(value);
    }  
    
    
    @Produces 
    @Property
    public AtomicReference<String> getWatchableStringParameter (InjectionPoint ip)
    {
        String value = getStringParameter(ip);
        String key = getKey(ip);
        AtomicReference<String> wrapped = new AtomicReference<>(value);
        ConfigurationReference<AtomicReference<String>> ref = new ConfigurationReference<>(wrapped, ip);
        List<ConfigurationReference<AtomicReference<String>>> refs = new ArrayList<>();
        if (strings.get(key) != null) {
            refs = strings.get(key);
        }
        refs.add(ref);
        strings.put(key, refs);
        return wrapped;
    }
    
    @Produces 
    @Property
    public AtomicReference<Boolean> getWatchableBooleanParameter (InjectionPoint ip)
    {
        Boolean value = getBooleanParameter(ip);
        String key = getKey(ip);
        AtomicReference<Boolean> wrapped = new AtomicReference<>(value);
        ConfigurationReference<AtomicReference<Boolean>> ref = new ConfigurationReference<>(wrapped, ip);
        List<ConfigurationReference<AtomicReference<Boolean>>> refs = new ArrayList<>();
        if (booleans.get(key) != null) {
            refs = booleans.get(key);
        }
        refs.add(ref);
        booleans.put(key, refs);
        return wrapped;
    }
    
    @Produces 
    @Property
    public AtomicReference<Float> getWatchableFloatsParameter (InjectionPoint ip)
    {
        Float value = getFloat(ip);
        String key = getKey(ip);
        AtomicReference<Float> wrapped = new AtomicReference<>(value);
        ConfigurationReference<AtomicReference<Float>> ref = new ConfigurationReference<>(wrapped, ip);
        List<ConfigurationReference<AtomicReference<Float>>> refs = new ArrayList<>();
        if (floats.get(key) != null) {
            refs = floats.get(key);
        }
        refs.add(ref);
        floats.put(key, refs);
        return wrapped;
    }
    
    @Produces 
    @Property
    public AtomicReference<BigDecimal> getWatchableBigDecimalParameter (InjectionPoint ip)
    {
        BigDecimal value = getBigDecimal(ip);
        String key = getKey(ip);
        AtomicReference<BigDecimal> wrapped = new AtomicReference<>(value);
        ConfigurationReference<AtomicReference<BigDecimal>> ref = new ConfigurationReference<>(wrapped, ip);
        List<ConfigurationReference<AtomicReference<BigDecimal>>> refs = new ArrayList<>();
        if (bigs.get(key) != null) {
            refs = bigs.get(key);
        }
        refs.add(ref);
        bigs.put(key, refs);
        return wrapped;
    }
    
    @Produces 
    @Property
    public AtomicReference<Integer> getWatchableIntegerParameter (InjectionPoint ip)
    {
        Integer value = getIntegerParameter(ip);
        String key = getKey(ip);
        AtomicReference<Integer> wrapped = new AtomicReference<>(value);
        ConfigurationReference<AtomicReference<Integer>> ref = new ConfigurationReference<>(wrapped, ip);
        List<ConfigurationReference<AtomicReference<Integer>>> refs = new ArrayList<>();
        if (integers.get(key) != null) {
            refs = integers.get(key);
        }
        refs.add(ref);
        integers.put(key, refs);
        return wrapped;
    }
    
    @Produces 
    @Property
    public AtomicReference<Double> getWatchableDoubleParameter (InjectionPoint ip)
    {
        Double value = getDoubleParameter(ip);
        String key = getKey(ip);
        AtomicReference<Double> wrapped = new AtomicReference<>(value);
        ConfigurationReference<AtomicReference<Double>> ref = new ConfigurationReference<>(wrapped, ip);
        List<ConfigurationReference<AtomicReference<Double>>> refs = new ArrayList<>();
        if (doubles.get(key) != null) {
            refs = doubles.get(key);
        }
        refs.add(ref);
        doubles.put(key, refs);
        return wrapped;
    }
    
    private String getKey (InjectionPoint ip) {
        Property annot1 = ip.getAnnotated().getAnnotation(Property.class);
        String key = annot1.value();
        if (key.isEmpty()) {
            // Use field as default key
            key = ip.getMember().getName();
        }
        return key;
    }
    
    @Produces 
    @Property
    @StringProperty
    public String getStringParameter (InjectionPoint ip)
    {
        StringProperty annot = ip.getAnnotated().getAnnotation(StringProperty.class);
        Property annot1 = ip.getAnnotated().getAnnotation(Property.class);
        String key = annot == null ? annot1.value() : annot.value();
        if (key.isEmpty()) {
            key = ip.getMember().getName();
        }
        boolean isEncrypted = annot == null ? annot1.encrypted() : annot.encrypted();
        String value = getValue(annot == null ? annot1.defaultValue() : annot.defaultValue(), key);
        return config.parse(isEncrypted, value);
    }
    
    @Produces 
    @Property
    @IntegerProperty
    public Integer getIntegerParameter (InjectionPoint ip)
    {
        IntegerProperty annot = ip.getAnnotated().getAnnotation(IntegerProperty.class);
        Property annot1 = ip.getAnnotated().getAnnotation(Property.class);
        String key = annot == null ? annot1.value() : annot.value();
        if (key.isEmpty()) {
            key = ip.getMember().getName();
        }
        String value = getValue(annot == null ? annot1.defaultValue() : annot.defaultValue(), key);
        return value == null ? null : Integer.parseInt(value);
    }
    
    @Produces
    @Property
    @BooleanProperty
    public Boolean getBooleanParameter (InjectionPoint ip)
    {
        BooleanProperty annot = ip.getAnnotated().getAnnotation(BooleanProperty.class);
        Property annot1 = ip.getAnnotated().getAnnotation(Property.class);
        String key = annot == null ? annot1.value() : annot.value();
        if (key.isEmpty()) {
            key = ip.getMember().getName();
        }
        String value = getValue(annot == null ? annot1.defaultValue() : annot.defaultValue(), key);
        return value == null ? null : Boolean.parseBoolean(value);
    }
    
    @Produces 
    @Property
    @DoubleProperty
    public Double getDoubleParameter (InjectionPoint ip)
    {
        DoubleProperty annot = ip.getAnnotated().getAnnotation(DoubleProperty.class);
        Property annot1 = ip.getAnnotated().getAnnotation(Property.class);
        String key = annot == null ? annot1.value() : annot.value();
        if (key.isEmpty()) {
            key = ip.getMember().getName();
        }
        String value = getValue(annot == null ? annot1.defaultValue() : annot.defaultValue(), key);
        return value == null ? null : Double.parseDouble(config.getProperty(key));
    }
    
    private String getValue (String defaultValue, String key) {
        String value = config.getProperty(key);
        if (value == null && !Objects.equals(defaultValue, ConfigurationManager.DEFAULT_VALUE)) {
            value = defaultValue;
        }
        return value;
    }
    
}
