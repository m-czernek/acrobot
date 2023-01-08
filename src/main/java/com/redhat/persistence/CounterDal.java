package com.redhat.persistence;

import com.redhat.entities.Counter;

import javax.persistence.EntityManager;

public class CounterDal {
    private EntityManager em;

    public CounterDal() {
        this.init();
    }

    public void logMessage(String userEmail, String msg) {
        init();
        em.getTransaction().begin();
        try {
            Counter counter = new Counter(userEmail, msg);
            em.persist(counter);
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            em.getTransaction().rollback();
        } finally {
            close();
        }
    }

    public int getNumMessagesThisMonth() {
        init();
        try {
            return em.createNativeQuery("SELECT c.* FROM counter AS c WHERE MONTH(c.timestamp) = MONTH(NOW()) AND YEAR(c.timestamp) = YEAR(NOW())", Counter.class)
                    .getResultList()
                    .size();
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        } finally {
            close();
        }
    }

    public int getNumMessagesBetweenRange(int rangeStart) {
        init();
        try {

            return em.createNativeQuery("SELECT c.* FROM counter AS c WHERE c.timestamp >= NOW() - INTERVAL " + rangeStart + " month", Counter.class)
                    .getResultList()
                    .size();
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        } finally {
            close();
        }
    }

    public void init() {
        if(em == null) {
            em = PersistenceManager.INSTANCE.getEntityManager();
        }
    }

    public void close() {
        if(em != null) {
            em.close();
        }
        this.em = null;
    }
}
