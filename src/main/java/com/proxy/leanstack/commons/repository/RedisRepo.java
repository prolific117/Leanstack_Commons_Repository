/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proxy.leanstack.commons.repository;

import com.proxy.leanstack.commons.util.RequestUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 *
 * @author prolific
 */
public class RedisRepo {
    
       //address of your redis server
    private static final String redisHost = "localhost";
    private static final Integer redisPort = 6379;
    private static final Logger logger = Logger.getLogger(RequestUtils.class.getName());
 
    //the jedis connection pool..
    private static JedisPool pool = null;
 
    public RedisRepo() {
        //configure our pool connection
        pool = new JedisPool(redisHost, redisPort);
 
    }
    
    public void addSessionKey(String key, Object value) throws IOException {
        Jedis jedis = pool.getResource();
        jedis.set(convertToBytes(key), convertToBytes(value));
    }
 
    private byte[] convertToBytes(Object object){
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutput out = new ObjectOutputStream(bos)) {
             out.writeObject(object);
            return bos.toByteArray();
        } 
        catch(IOException ex){
            logger.log(Level.SEVERE, "Cannot check customer existence");
            return null;
        }
    }
    
    private Object convertFromBytes(byte[] bytes){
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             ObjectInput in = new ObjectInputStream(bis)) {
            return in.readObject();
        } 
        catch(IOException ex){
            logger.log(Level.SEVERE, "Cannot check customer existence");
            return null;
        }
        catch(ClassNotFoundException ex){
            logger.log(Level.SEVERE, "Cannot check customer existence");
            return null;
        }
    }
    
    public void checkService(){
        
    }
 
}
