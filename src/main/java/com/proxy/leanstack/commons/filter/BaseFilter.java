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

import com.proxy.leanstack.commons.security.CommonsServletRequest;
import java.io.IOException;
import java.util.Arrays;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

@WebFilter (urlPatterns = {"/*"}, asyncSupported = true)
public class BaseFilter implements Filter {
    
    public BaseFilter() {
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        //if the ServletRequest is an instance of HttpServletRequest
        if(request instanceof HttpServletRequest) {
            //cast the object
            HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            String contentType = httpServletRequest.getContentType();
            if (Arrays.asList("application/json", "application/x-www-form-urlencoded").contains(contentType)) {
                CommonsServletRequest commonsServletRequest = new CommonsServletRequest(httpServletRequest);
                //continue on in the filter chain with the FakeHeaderRequest and ServletResponse objects
                chain.doFilter(commonsServletRequest, response);
            } else {
                chain.doFilter(request, response);
            }
            //create the FakeHeadersRequest object to wrap the HttpServletRequest
        } else {
            //otherwise, continue on in the chain with the ServletRequest and ServletResponse objects
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

