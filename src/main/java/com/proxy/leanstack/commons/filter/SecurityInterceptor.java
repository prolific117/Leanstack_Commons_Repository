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


import com.fasterxml.jackson.databind.ObjectMapper;
import com.proxy.leanstack.commons.annotations.Property;
import com.proxy.leanstack.commons.security.Secured;
import com.proxy.leanstack.commons.security.SessionDetail;
import com.proxy.leanstack.commons.security.SessionDetailProvider;
import com.proxy.leanstack.commons.security.SignatureMethod;
import com.proxy.leanstack.commons.util.HashUtils;
import com.proxy.leanstack.commons.util.RequestUtils;
import com.proxy.leanstack.commons.util.UtilityObject;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;
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
@Secured
@Priority(Priorities.AUTHENTICATION + 20)
public class SecurityInterceptor implements ContainerRequestFilter {

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
    
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper ();
    
    private static final Logger logger = Logger.getLogger(SecurityInterceptor.class.getName());
    
    public SecurityInterceptor() {
    }

    @Override
    public void filter(ContainerRequestContext ctx) throws IOException { 
        session.setClientIp(UtilityObject.getIpFromRequest(this.request));
        Secured secured = resourceInfo.getResourceMethod().getAnnotation(Secured.class);
        if (secured == null) {
            secured = resourceInfo.getResourceClass().getAnnotation(Secured.class);
        }
        String sessionID = request.getHeader("sessionId");
        if (secured.optional() && (sessionID == null || sessionID.isEmpty())) {
            return;
        }
        String deviceID = request.getHeader("deviceId");
        if (secured.signed() && !checkSignature(secured, ctx)) {
            Response failureResponse = Response.ok(sessionDetailProvider.getInvalidSignature(), MediaType.APPLICATION_JSON).build();
            ctx.abortWith(failureResponse);
            return;
        }
        if (sessionID == null || sessionID.isEmpty()) {
            ctx.abortWith(Response.ok(sessionDetailProvider.getUnauthenticatedMessage(), MediaType.APPLICATION_JSON).build());
            return;
        } 
        SessionDetail sessionDetail = sessionDetailProvider.getSession(sessionID);
        if (sessionDetail == null) {
            Response failureResponse = Response.ok(sessionDetailProvider.getUnauthenticatedMessage(), MediaType.APPLICATION_JSON).build();
            ctx.abortWith(failureResponse);
            return;
        } 
        if (!sessionDetail.getIsActive()) {
            Response failureResponse = Response.ok(sessionDetailProvider.getExpiredMessage(), MediaType.APPLICATION_JSON).build();
            ctx.abortWith(failureResponse);
            return;
        }
        sessionDetail.setSessionId(sessionID);
        sessionDetail.setDeviceId(deviceID);
        // Check for annotation at class and signatureMethods level
        String[] required = secured.permissions();
        if (!sessionDetail.hasAnyPermission(required)) {
            Response failureResponse = Response.ok(sessionDetailProvider.getUnauthorizedMessage(), MediaType.APPLICATION_JSON).build();
            ctx.abortWith(failureResponse);
            return;
        }
        session.merge(sessionDetail);
    }
    
    private boolean checkSignature (Secured secured, ContainerRequestContext ctx) throws IOException {
        Boolean supportOldApps = commonsOptionalSignature.get();
        if (appSignatureKey == null) {
            logger.log(Level.SEVERE, "Developer has not included commonsSignatureKey in the property file. "
                    + "Commons will make request as invalid. Either set signed to false on the @Secured annotation or include the key");
            return false;
        }
        String signature = request.getHeader("Signature");
        SignatureMethod method = secured.signatureMethod();
        if (signature == null && supportOldApps) {
            logger.log(Level.FINEST, "Request signature not verfied because commonsOptionalSignature is set to true");
            return true;
        }
        if (signature == null || method  == null) {
            logger.log(Level.FINEST, "Authentication failing due to null or unsupported signature or signaturemethod header");
            return false;
        }
        MediaType type = ctx.getMediaType();
        Map<String, Object> object = RequestUtils.getData(ctx);
        if (object == null){
            logger.log(Level.WARNING, "Endpoint {0} marked as signed but could read stream as json or form encoded", ctx.getUriInfo().getAbsolutePath().toString());
            return false;
        }
        StringBuilder builder = new StringBuilder();
        String[] signFields = secured.signatureFields();
        for (String field : signFields) {
            builder.append(object.get(field));
        }
        boolean isValid = HashUtils.checkSignature(method, signature, builder.toString(), appSignatureKey);
        return isValid;
    }
}
