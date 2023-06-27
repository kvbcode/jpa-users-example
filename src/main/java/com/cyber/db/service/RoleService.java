package com.cyber.db.service;

import com.cyber.db.entity.Role;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

public class RoleService {
    private final EntityManagerFactory entityManagerFactory;

    public RoleService(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public Role createOrUpdate(Role role) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            entityManager.getTransaction().begin();
            role = entityManager.merge(role);
            entityManager.getTransaction().commit();
        }
        return role;
    }

    public void removeById(String nameId) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            entityManager.getTransaction().begin();
            Role roleReference = entityManager.getReference(Role.class, nameId.toUpperCase());
            entityManager.remove(roleReference);
            entityManager.getTransaction().commit();
        }
    }
}
