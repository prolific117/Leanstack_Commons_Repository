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

public abstract class AuthenticatedUser  {

    private Long id;
    
    private String userName;
    
    private String coreBankingId;
    
    public AuthenticatedUser (Long id, String userName, String coreBankingId) {
        this.id = id;
        this.userName = userName;
        this.coreBankingId = coreBankingId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCoreBankingId() {
        return coreBankingId;
    }

    public void setCoreBankingId(String coreBankingId) {
        this.coreBankingId = coreBankingId;
    }
    
}
