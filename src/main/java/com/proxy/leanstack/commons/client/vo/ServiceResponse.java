/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proxy.leanstack.commons.client.vo;

/**
 *
 * @author prolific
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
public class ServiceResponse
{

    public static final int SUCCESS = 0;
    public static final int ERROR = 10;

    /**
     *
     */
    public static final int AUTHENTICATION_ERROR = 20;
    public static final int AUTHORIZATION_ERROR = 40;
    public static final int INCOMPLETE_ERROR = 50;
    
    public static final String GENERAL_ERROR_MESSAGE = "Request processing error";
    public static final String SERVICE_ERROR_MESSAGE = "Web Service Error";
    public static final String INCORRECT_PASSWORD = "Invalid username or password";
    public static final String INVALID_RESPONSE_ERROR_MESSAGE = "Invalid service response";
    public static final String GENERAL_SUCCESS_MESSAGE = "Operation Successful";
    
    
    private int code;

    private String description;

    public ServiceResponse(int code)
    {
        this.code = code;
    }

    public ServiceResponse(int code, String description)
    {
        this(code);
        this.description = description;
    }

    public int getCode()
    {
        return code;
    }

    public void setCode(int code)
    {
        this.code = code;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

}

