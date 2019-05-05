package com.redhat.persistence;

import com.redhat.entities.Acronym;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;

public class AcronymDAL {

    private EntityManager em;

    public AcronymDAL() {
        init();
    }

    public List<Acronym> getAcronymsByName(String acronym) {
        init();
        try {
            List<Acronym> res = em.createNamedQuery("findAcronymByName", Acronym.class)
                    .setParameter(1, acronym)
                    .getResultList();
            close();
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            close();
            return Collections.emptyList();
        }
    }

    public void saveAcronym(Acronym acronym) {
        init();
        em.getTransaction().begin();
        try {
           em.persist(acronym);
           em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            em.getTransaction().rollback();
        }
        close();
    }

    public void updateAcronym(Acronym acronym) {
        init();
        em.getTransaction().begin();
        try {
            em.merge(acronym);
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            em.getTransaction().rollback();
        }
        close();
    }

    public void init() {
        if(em == null) {
            em = PersistenceManager.INSTANCE.getEntityManager();
        }
    }

    public void close() {
        em.close();
        this.em = null;
    }

}
