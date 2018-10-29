/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proxy.leanstack.commons.filter;

/**
 *
 * @author prolific
 */

import com.proxy.leanstack.commons.base.ConfigurationManager;
import com.proxy.leanstack.commons.annotations.Property;
import com.proxy.leanstack.commons.security.SessionDetail;
import com.proxy.leanstack.commons.security.SessionDetailProvider;
import com.proxy.leanstack.commons.util.RequestUtils;
import com.proxy.leanstack.commons.util.UtilityObject;
import com.proxy.leanstack.commons.validation.Validatable;
import com.proxy.leanstack.commons.validation.Validated;
import java.io.IOException;
import java.util.FormatFlagsConversionMismatchException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Priority;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;


@Provider
@Validated
@Priority(Priorities.AUTHENTICATION)
public class ValidationInterceptor implements ContainerRequestFilter {

    @Inject
    private SessionDetailProvider sessionDetailProvider;
    
    @Inject
    @Property (value = "commonsSignatureKey")
    private String appSignatureKey;
    
    @Inject
    @Property (defaultValue = "false")
    private AtomicReference<Boolean> commonsOptionalSignature;
    
    @Context
    private HttpServletRequest request;
    
    @Context
    ResourceInfo resourceInfo;
    
    @Inject
    SessionDetail session;
    
    private static final Logger logger = Logger.getLogger(ValidationInterceptor.class.getName());
    
    @Inject
    @Property(defaultValue = "%s is required")
    private AtomicReference<String> commonsValidationRequiredMessageFormat;

    @Inject
    @Property(defaultValue = "%s should be at least %s characters")
    private AtomicReference<String> commonsValidationMinLengthMessageFormat;
    
    @Inject
    @Property(defaultValue = "%s should not be more than %s characters")
    private AtomicReference<String> commonsValidationMaxLengthMessageFormat;
    
    @Inject
    @Property(defaultValue = "%s is not valid")
    private AtomicReference<String> commonsValidationRegexMessageFormat;
    
    private static final Map<String, Pattern> PATTERNS = new ConcurrentHashMap<>(); 
    
    public ValidationInterceptor() {
    }

    @Override
    public void filter(ContainerRequestContext ctx) throws IOException { 
        session.setClientIp(UtilityObject.getIpFromRequest(this.request));
        if (!checkValidated(ctx)) {
            return;
        }
    }
    
    private boolean checkValidated (ContainerRequestContext ctx) {
        Validated validated = resourceInfo.getResourceMethod().getAnnotation(Validated.class);
        if (validated == null) {
            return true;
        }
        Map<String, Object> object = RequestUtils.getData(ctx);
        if (object == null) {
            ctx.abortWith(Response.ok(sessionDetailProvider.getValidationMessage("Content type not supported or empty content"), MediaType.APPLICATION_JSON).build());
            return false;
        }
        Validatable[] validatables = validated.value();
        for (Validatable v : validatables) {
            String field = v.field();
            Object value = object.get(field);
            try {
                if (value == null || String.valueOf(value).isEmpty()) {
                ctx.abortWith(Response.ok(sessionDetailProvider.getValidationMessage(String.format(commonsValidationRequiredMessageFormat.get(), 
                        v.fieldName())), MediaType.APPLICATION_JSON).build());
                    return false;
                } 
                if (String.valueOf(value).trim().length() < v.minLength()) {
                    ctx.abortWith(Response.ok(sessionDetailProvider.getValidationMessage(String.format(commonsValidationMinLengthMessageFormat.get(), v.fieldName(), 
                            v.minLength())), MediaType.APPLICATION_JSON).build());
                    return false;
                }
                if (String.valueOf(value).trim().length() > v.maxLength()) {
                    ctx.abortWith(Response.ok(sessionDetailProvider.getValidationMessage(String.format(commonsValidationMaxLengthMessageFormat.get(), v.fieldName(), 
                            v.maxLength())), MediaType.APPLICATION_JSON).build());
                    return false;
                }
                if (!Objects.equals(v.regex(), ConfigurationManager.DEFAULT_VALUE)) {
                    String regex = v.regex();
                    Pattern pattern = PATTERNS.get(regex);
                    if (pattern == null) {
                        pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
                        Matcher matcher = pattern.matcher(String.valueOf(value));
                        if (!matcher.matches()) {
                            ctx.abortWith(Response.ok(sessionDetailProvider.getValidationMessage(String.format(commonsValidationRegexMessageFormat.get(), v.fieldName())), MediaType.APPLICATION_JSON).build());
                            return false;
                        }
                    }
                }
            } catch (FormatFlagsConversionMismatchException e) {
                logger.log(Level.SEVERE, "Please check all validation message meet format specification in API doc", e);
                throw e;
            }
        }
        return true;
    }
 
}

