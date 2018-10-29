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

public interface SessionDetailProvider {
    
    public SessionDetail getSession (String sessionKey);
    
    public Object getUnauthenticatedMessage ();
    
    public Object getUnauthorizedMessage ();
    
    public Object getExpiredMessage ();
    
    public Object getInvalidSignature ();
    
    public Object getValidationMessage (String message);
    
    public Object getAppVersionCheckFailMessage ();
    
}
