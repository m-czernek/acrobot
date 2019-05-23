package com.redhat.persistence;

import com.redhat.entities.Acronym;
import com.redhat.entities.Explanation;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;

public class AcronymDAL {

    private EntityManager em;

    public AcronymDAL() {
        init();
    }

    // Acronym is checked in lowercase form, since the query also contains lowercase form.
    // This is such that $ACRONYM is not duplicated with $acronym.
    public List<Acronym> getAcronymsByName(String acronym) {
        init();
        try {
            return em.createNamedQuery("findAcronymByName", Acronym.class)
                    .setParameter(1, acronym.toLowerCase())
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        } finally {
            close();
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

    public void deleteExplanation(Explanation explanation) {
        init();
        em.getTransaction().begin();
        try {
            explanation.setAcronym(null);
            explanation = em.merge(explanation);
            em.remove(explanation);
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            em.getTransaction().rollback();
        }
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
