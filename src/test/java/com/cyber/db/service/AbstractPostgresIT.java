package com.cyber.db.service;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.HashMap;

public abstract class AbstractPostgresIT {
    private static PostgreSQLContainer<?> postgreSQLContainer;
    private static EntityManagerFactory emf;

    public static void startContainer() {
        postgreSQLContainer = new PostgreSQLContainer<>("postgres:15.2");
        postgreSQLContainer.start();

        HashMap<String, String> properties = new HashMap<>();
        properties.put("jakarta.persistence.jdbc.url", postgreSQLContainer.getJdbcUrl());
        properties.put("jakarta.persistence.jdbc.user", postgreSQLContainer.getUsername());
        properties.put("jakarta.persistence.jdbc.password", postgreSQLContainer.getPassword());
        emf = Persistence.createEntityManagerFactory("jpa_users_postres_tc", properties);
    }

    public static void stopContainer() {
        emf.close();
        postgreSQLContainer.close();
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        return emf;
    }

}
