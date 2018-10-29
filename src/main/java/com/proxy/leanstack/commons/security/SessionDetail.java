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



import java.util.Arrays;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.servlet.http.HttpServletRequest;

@RequestScoped
public class SessionDetail {
    
    private String clientIp;
    
    private String sessionId;
    
    private String deviceId;
    
    private String locale = "en";
    
    private AuthenticatedUser principal;
    
    private String[] permissions;
    
    private Boolean isActive;

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public AuthenticatedUser getPrincipal() {
        return principal;
    }

    public void setPrincipal(AuthenticatedUser principal) {
        this.principal = principal;
    }

    public String[] getPermissions() {
        return permissions;
    }

    public void setPermissions(String[] permissions) {
        this.permissions = permissions;
    }
    
    public boolean hasAnyPermission (String[] permissions) {
       if (permissions == null || permissions.length == 0) {
           // No special permisions required
           return true;
       }
       List<String> all = Arrays.asList(permissions);
       for (String permission : this.permissions) {
           if (all.contains(permission)) {
               return true;
           }
       }
       return false;
    }
    
    public void merge (SessionDetail detail) {
        if (detail == null) {
            return;
        }
        setIsActive(detail.getIsActive());
        setSessionId(detail.getSessionId());
        setDeviceId(detail.getDeviceId());
        setPermissions(detail.getPermissions());
        setPrincipal(detail.getPrincipal());
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
    
}
