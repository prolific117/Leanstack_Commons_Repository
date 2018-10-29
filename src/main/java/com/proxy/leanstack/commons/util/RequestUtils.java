/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proxy.leanstack.commons.util;

/**
 *
 * @author prolific
 */


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.proxy.leanstack.commons.security.CommonsServletRequest;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.MediaType;


public class RequestUtils {

    private static final Logger LOG = Logger.getLogger(RequestUtils.class.getName());
    
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper ();
    
    public static final Map<String, Object> mapUrlEncoded (String data) throws Exception {
        HashMap<String, Object> response = new HashMap<>();
        String[] payload = data.split("&");
        for (int i = 0; i < payload.length; i++) {
            String[] keyVal = payload[i].split("=");
            if(keyVal.length > 1 && keyVal[1] != null)
                response.put(keyVal[0], URLDecoder.decode(keyVal[1], "UTF-8"));
            else
                response.put(keyVal[0], "");
        }
        return response;   
    }
    
    public static Map<String, Object> getData (ContainerRequestContext ctx) {
        CommonsServletRequest.ServletInputStreamWrapper entity = (CommonsServletRequest.ServletInputStreamWrapper) ctx.getEntityStream();
        String data = new String(entity.getData());
        MediaType type = ctx.getMediaType();
        Map<String, Object> object = null;
        try {
            if (type.isCompatible(MediaType.APPLICATION_FORM_URLENCODED_TYPE)) {
            
                object = RequestUtils.mapUrlEncoded(data);
            } else if (type.isCompatible(MediaType.APPLICATION_JSON_TYPE)) {
                TypeReference<HashMap<String,Object>> typeRef 
                    = new TypeReference<HashMap<String,Object>>() {};
                object = JSON_MAPPER.readValue(data, typeRef);
            } else {
                LOG.log(Level.WARNING, "Content type unsupported for mapping {0}", type.toString());
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error occured with mapping data", e);
        }
        return object;
    }
}

