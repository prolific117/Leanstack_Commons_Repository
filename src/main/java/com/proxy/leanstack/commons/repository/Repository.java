/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proxy.leanstack.commons.repository;

/**
 *
 * @author prolific
 */


import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

public abstract class Repository <T extends AbstractRepositoryModel> {
    
    public abstract Logger log ();
    
    public abstract EntityManager em ();
      
    public T first (List<T> results) {
        return results != null && results.size() > 0 ? results.get(0) : null;
    }
    
    public T findOne (Long id, Class<T> clazz) {
        try {
            CriteriaBuilder builder = em().getCriteriaBuilder();
            CriteriaQuery<T> query = builder.createQuery(clazz);
            Root<T> root = query.from(clazz);
            query.select(root);
            query.where(builder.equal(root.get("id"), id));
            T result = em().createQuery(query).getSingleResult();
            return result;
        } catch (NoResultException e) {
            return null;
        }
    }
    
    public T findByParam(String param, String value, Class<T> clazz){
        try {
            CriteriaBuilder builder = em().getCriteriaBuilder();
            CriteriaQuery<T> query = builder.createQuery(clazz);
            Root<T> root = query.from(clazz);
            query.select(root);
            query.where(builder.equal(root.get(param), value));
            T result = em().createQuery(query).getSingleResult();
            return result;
        } catch (NoResultException e) {
            return null;
        }
    }
    
    @Transactional
    public T add(T object) {
        if (object == null) {
            log().severe("Attempting to save a null object. Probably a bug in code.");
            throw new IllegalArgumentException("Null value supplied");
        }
        if (object.getId() != null) {
           log().warning("Attempting to persist a manually generated id %s. "
                   + "This may collide with database id generation strategy");
        }
        Date currentTime = new Date ();
        if (object.getCreateDate() == null) {   
            object.setCreateDate(currentTime);
        }
        // Always update last update time
        object.setLastUpdateDate(currentTime);
        em().persist(object);
        return object;
    }
    
    public void remove(T object) {
        em().remove(object);
    }
   
    @Transactional
    public T update(T object) {
        if (object == null) {
            log().severe("Attempting to save a null object. Probably a bug in code.");
            throw new IllegalArgumentException("Null value supplied");
        }
        if (object.getId() == null) {
            log().warning("Attempting to persist a manually generated id %s. "
                   + "This may collide with database id generation strategy");
           throw new IllegalArgumentException ("Object not saved. You have to add the object before updating");
        }
        Date currentTime = new Date ();
        if (object.getCreateDate() == null) {
            object.setCreateDate(currentTime);
        }
        // Always update last update time
        object.setLastUpdateDate(currentTime);
        em().merge(object);
        return object;
    }
    
    public List<T> findAll (int page, int size, Class<T> clazz) {
        CriteriaBuilder builder = em().getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(clazz);
        Root<T> root = query.from(clazz);
        query.select(root);
        List<T> results = em().createQuery(query)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
        return results;
    }
    
    public List<T> findAll (Class<T> clazz) {
        CriteriaBuilder builder = em().getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(clazz);
        Root<T> root = query.from(clazz);
        query.select(root);
        List<T> results = em().createQuery(query)
                .getResultList();
        return results;
    }
    
}


