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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ReadListener;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class CommonsServletRequest extends HttpServletRequestWrapper {

    private HttpServletRequest req;

    public class ServletInputStreamWrapper extends ServletInputStream {

        private byte[] data;

        private int idx = 0;

        private ReadListener readListener;

        ServletInputStreamWrapper(byte[] data) {
            if (data == null) {
                data = new byte[0];
            }
            this.data = data;
        }

        @Override
        public int read() throws IOException {
            if (idx == data.length) {
                return -1;
            }
            return data[idx++];
        }

        @Override
        public boolean isFinished() {
            return false;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void reset() {
            
        }

        @Override
        public void setReadListener(ReadListener listener) {
            
        }
        
        public byte [] getData () {
            return data;
        }

    }

    private byte[] contentData;

    private HashMap<String, String[]> parameters;

    public CommonsServletRequest(HttpServletRequest request) {
        super(request);
        if (request == null) {
            throw new IllegalArgumentException("The HttpServletRequest is null!");
        }
        req = request;
    }

    /**
     * Returns the wrapped HttpServletRequest. Using the getParameterXXX(),
     * getInputStream() or getReader() methods may interfere with this class
     * operation.
     *
     * @return The wrapped HttpServletRequest.
     */
    public HttpServletRequest getRequest() {
        try {
            parseRequest();
        } catch (IOException e) {
            throw new IllegalStateException("Cannot parse the request!", e);
        }
        return new CommonsServletRequest(req);
    }

    /**
     * This method is safe to use multiple times. Changing the returned array
     * will not interfere with this class operation.
     *
     * @return The cloned content data.
     */
    public byte[] getContentData() {
        return contentData.clone();
    }

    /**
     * This method is safe to use multiple times. Changing the returned map or
     * the array of any of the map's values will not interfere with this class
     * operation.
     *
     * @return The clonned parameters map.
     */
    public HashMap<String, String[]> getParameters() {
        HashMap<String, String[]> map = new HashMap<>(parameters.size() * 2);
        for (String key : parameters.keySet()) {
            map.put(key, parameters.get(key).clone());
        }
        return map;
    }

    private void parseRequest() throws IOException {
        if (contentData != null) {
            return; //already parsed
        }
        byte[] data = new byte[req.getContentLength()];
        int len = 0, totalLen = 0;
        InputStream is = req.getInputStream();
        while (totalLen < data.length) {
            totalLen += (len = is.read(data, totalLen, data.length - totalLen));
            if (len < 1) {
                throw new IOException("Cannot read more than " + totalLen + (totalLen == 1 ? " byte!" : " bytes!"));
            }
        }
        contentData = data;
        String enc = req.getCharacterEncoding();
        if (enc == null) {
            enc = "UTF-8";
        }
        String s = new String(data, enc), name, value;
        StringTokenizer st = new StringTokenizer(s, "&");
        int i;
        HashMap<String, LinkedList<String>> mapA = new HashMap<>(data.length * 2);
        LinkedList<String> list;
        boolean decode = req.getContentType() != null && req.getContentType().equals("application/x-www-form-urlencoded");
        while (st.hasMoreTokens()) {
            s = st.nextToken();
            i = s.indexOf("=");
            if (i > 0 && s.length() > i + 1) {
                name = s.substring(0, i);
                value = s.substring(i + 1);
                if (decode) {
                    try {
                        name = URLDecoder.decode(name, "UTF-8");
                    } catch (Exception e) {
                    }
                    try {
                        value = URLDecoder.decode(value, "UTF-8");
                    } catch (Exception e) {
                    }
                }
                list = mapA.get(name);
                if (list == null) {
                    list = new LinkedList<String>();
                    mapA.put(name, list);
                }
                list.add(value);
            }
        }
        HashMap<String, String[]> map = new HashMap<String, String[]>(mapA.size() * 2);
        for (String key : mapA.keySet()) {
            list = mapA.get(key);
            map.put(key, list.toArray(new String[list.size()]));
        }
        parameters = map;
    }

    /**
     * This method is safe to call multiple times. Calling it will not interfere
     * with getParameterXXX() or getReader(). Every time a new
     * ServletInputStream is returned that reads data from the begining.
     *
     * @return A new ServletInputStream.
     * @throws java.io.IOException
     */
    @Override
    public ServletInputStream getInputStream() throws IOException {
        parseRequest();
        return new ServletInputStreamWrapper(contentData);
    }

    /**
     * This method is safe to call multiple times. Calling it will not interfere
     * with getParameterXXX() or getInputStream(). Every time a new
     * BufferedReader is returned that reads data from the begining.
     *
     * @return A new BufferedReader with the wrapped request's character
     * encoding (or UTF-8 if null).
     * @throws java.io.IOException
     */
    @Override
    public BufferedReader getReader() throws IOException {
        parseRequest();
        String enc = req.getCharacterEncoding();
        if (enc == null) {
            enc = "UTF-8";
        }
        return new BufferedReader(new InputStreamReader(new ByteArrayInputStream(contentData), enc));
    }

    /**
     * This method is safe to execute multiple times.
     *
     * @param name
     * @return
     * @see javax.servlet.ServletRequest#getParameter(java.lang.String)
     */
    @Override
    public String getParameter(String name) {
        try {
            parseRequest();
        } catch (IOException e) {
            throw new IllegalStateException("Cannot parse the request!", e);
        }
        String[] values = parameters.get(name);
        if (values == null || values.length == 0) {
            return null;
        }
        return values[0];
    }

    /**
     * This method is safe.
     *
     * @return
     * @see {@link #getParameters()}
     * @see javax.servlet.ServletRequest#getParameterMap()
     */
    @SuppressWarnings("unchecked")
    @Override
    public Map getParameterMap() {
        try {
            parseRequest();
        } catch (IOException e) {
            throw new IllegalStateException("Cannot parse the request!", e);
        }
        return getParameters();
    }

    /**
     * This method is safe to execute multiple times.
     *
     * @return
     * @see javax.servlet.ServletRequest#getParameterNames()
     */
    @SuppressWarnings("unchecked")
    @Override
    public Enumeration getParameterNames() {
        try {
            parseRequest();
        } catch (IOException e) {
            throw new IllegalStateException("Cannot parse the request!", e);
        }
        return new Enumeration<String>() {

            private String[] arr = getParameters().keySet().toArray(new String[0]);

            private int idx = 0;

            @Override
            public boolean hasMoreElements() {
                return idx < arr.length;
            }

            @Override
            public String nextElement() {
                return arr[idx++];
            }
        };
    }

    /**
     * This method is safe to execute multiple times. Changing the returned
     * array will not interfere with this class operation.
     *
     * @param name
     * @return
     * @see javax.servlet.ServletRequest#getParameterValues(java.lang.String)
     */
    @Override
    public String[] getParameterValues(String name) {
        try {
            parseRequest();
        } catch (IOException e) {
            throw new IllegalStateException("Cannot parse the request!", e);
        }
        String[] arr = parameters.get(name);
        if (arr == null) {
            return null;
        }
        return arr.clone();
    }

}
