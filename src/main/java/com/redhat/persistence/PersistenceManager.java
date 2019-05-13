package com.redhat.persistence;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

public enum PersistenceManager {
    INSTANCE;

    private EntityManagerFactory emFactory;
    private String jdbcUrl;
    private String jdbcUser;
    private String jdbcPassword;
    private String persistenceUnitName;
    private String DEFAULT_PU = "backend-pu";

    PersistenceManager() {
        jdbcUrl = System.getenv("JDBC_URL");
        jdbcUser = System.getenv("JDBC_USER");
        jdbcPassword = System.getenv("JDBC_PASSWORD");
        persistenceUnitName = System.getenv("PU");
        if(persistenceUnitName == null) {
            persistenceUnitName = DEFAULT_PU;
        }
        init();
    }

    public void init() {
        if(emFactory == null) {
            Map<String, String> persistenceMap = new HashMap<>();
            persistenceMap.put("javax.persistence.jdbc.url", jdbcUrl);
            persistenceMap.put("javax.persistence.jdbc.user", jdbcUser);
            persistenceMap.put("javax.persistence.jdbc.password", jdbcPassword);
            emFactory = Persistence.createEntityManagerFactory(persistenceUnitName, persistenceMap);
        }
    }

    public EntityManager getEntityManager() {
        if(emFactory == null) {
            init();
        }
        return emFactory.createEntityManager();
    }

    public void close() {
        emFactory.close();
        this.emFactory = null;
    }

}
