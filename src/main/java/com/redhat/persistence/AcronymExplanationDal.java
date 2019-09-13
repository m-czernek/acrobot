package com.redhat.persistence;

import com.redhat.constants.Constants;
import com.redhat.entities.Acronym;
import com.redhat.entities.Explanation;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;

public class AcronymExplanationDal {

    private EntityManager em;

    public AcronymExplanationDal() {
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

    public String saveAcronym(Acronym acronym) {
        init();
        em.getTransaction().begin();
        try {
           em.persist(acronym);
           em.getTransaction().commit();
           return Constants.ACRONYM_SAVED;
        } catch (Exception e) {
            e.printStackTrace();
            em.getTransaction().rollback();
            return "Could not save the acronym: ```\n" + getStackTraceMaxSize(e, 4000) + "\n```";
        } finally {
            close();
        }
    }

    public String updateAcronym(Acronym acronym) {
        init();
        em.getTransaction().begin();
        try {
            em.merge(acronym);
            em.getTransaction().commit();
            return Constants.ACRONYM_UPDATED;
        } catch (Exception e) {
            e.printStackTrace();
            em.getTransaction().rollback();
            return "Could not update the acronym: ```\n" + getStackTraceMaxSize(e, 4000) + "\n```";
        } finally {
            close();
        }
    }

    public String deleteExplanation(Explanation explanation) {
        init();
        em.getTransaction().begin();
        try {
            explanation.setAcronym(null);
            explanation = em.merge(explanation);
            em.remove(explanation);
            em.getTransaction().commit();
            return Constants.EXPLANATION_REMOVED;
        } catch (Exception e) {
            e.printStackTrace();
            em.getTransaction().rollback();
            return "Could not delete the acronym: ```\n" + getStackTraceMaxSize(e, 4000) + "\n```";
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
        em.close();
        this.em = null;
    }

    private String getStackTraceMaxSize(Throwable e, int maxSize) {
        String truncatedMsg = "\nThis stacktrace has been truncated...";
        String stackTrace = getStackTraceAsString(e);
        if(stackTrace.length() > maxSize) {
            stackTrace = stackTrace.substring(0,maxSize - truncatedMsg.length());
            stackTrace += truncatedMsg;
        }
        return stackTrace;
    }

    private String getStackTraceAsString(Throwable e) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : e.getStackTrace()) {
            sb.append(element.toString());
            sb.append("\n");
        }

        if(e.getCause() != null) {
            sb.append("Caused by: \n");
            sb.append(getStackTraceAsString(e.getCause()));
        }
        return sb.toString();
    }

}
