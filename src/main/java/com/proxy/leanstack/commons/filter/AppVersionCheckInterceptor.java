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
/*
 * Filter to check app version support for a method.
 */

import com.proxy.leanstack.commons.annotations.Property;
import com.proxy.leanstack.commons.security.SessionDetailProvider;
import java.io.IOException;
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
import com.proxy.leanstack.commons.versioncheck.AppVersionCheck;

@Provider
@AppVersionCheck
@Priority(Priorities.AUTHENTICATION - 10)
public class AppVersionCheckInterceptor implements ContainerRequestFilter {
    @Context
    private HttpServletRequest request;
    
    @Context
    ResourceInfo resourceInfo;
    
    @Inject
    private SessionDetailProvider sessionDetailProvider;
    
    @Inject
    @Property("minAppVersionSupported")
    private AtomicReference<String> minAppVersion;
    
    @Inject
    @Property(value = "appVersionHeaderName", defaultValue = "appVersion")
    private String parameterName;
    
    private static final Logger logger = Logger.getLogger(AppVersionCheckInterceptor.class.getName());
    
    public AppVersionCheckInterceptor() {}
    
    @Override
    public void filter(ContainerRequestContext crc) throws IOException {
        AppVersionCheck cavs = resourceInfo.getResourceMethod().getAnnotation(AppVersionCheck.class);
        if (cavs == null) {
            cavs = resourceInfo.getResourceClass().getAnnotation(AppVersionCheck.class);
        }
        
        if(cavs == null) {
            return;
        }
        
        String appVersion = request.getHeader(parameterName);
        if(appVersion == null) {
            if(request.getParameterMap().containsKey(parameterName)) {
                String[] values = request.getParameterMap().get(parameterName);
                if(values != null && values.length > 0)
                    appVersion = values[0];
            }
        }
        
        if(appVersion == null || appVersion.isEmpty()) {
            if(cavs.allowToProceed()) {
                return;
            }
            if(getMinAppVersion() != null && !getMinAppVersion().isEmpty()) {
                Response failureResponse = Response.ok(sessionDetailProvider.getAppVersionCheckFailMessage(), MediaType.APPLICATION_JSON).build(); //Response.ok("YOUR APP VERSION IS NO LONGER SUPPORTED. KINDLY UPDATE. THANK YOU.", MediaType.APPLICATION_JSON).build();
                crc.abortWith(failureResponse);
            }
            return;
        }
        
        if(getMinAppVersion() != null && !getMinAppVersion().isEmpty()) {
            // compare versions here
            String[] appVersionArr = appVersion.split("\\.");
            String[] minAppVersionArr = getMinAppVersion().split("\\.");
            boolean supported = true;
            for(int i=0; i<minAppVersionArr.length; i++) {
                try {
                    int minAppINT = Integer.parseInt(minAppVersionArr[i]);
                    if(appVersionArr.length > i) {
                        int appINT = Integer.parseInt(appVersionArr[i]);
                        if(appINT < minAppINT) {
                            supported = false;
                            break;
                        }
                        if(i == appVersionArr.length) {
                            break;
                        }
                    } else {
                        break;
                    }
                } catch(Exception ex) {
                    logger.log(Level.SEVERE, "EXCEPTION COMPARING APP VERSION AGAINST PROXY'S MINIMUM SUPPORTED VERSION", ex);
                }
            }
            
            if(!supported && !cavs.allowToProceed()) {
                Response failureResponse = Response.ok(sessionDetailProvider.getAppVersionCheckFailMessage(), MediaType.APPLICATION_JSON).build(); // Response.ok("YOUR APP VERSION IS NO LONGER SUPPORTED. KINDLY UPDATE. THANK YOU.", MediaType.APPLICATION_JSON).build();
                crc.abortWith(failureResponse);
            }
        }
    }
    
    public String getMinAppVersion() {
        return minAppVersion.get();
    }
}
