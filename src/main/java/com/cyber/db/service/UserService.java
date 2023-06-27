package com.cyber.db.service;

import com.cyber.db.entity.Role;
import com.cyber.db.entity.User;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;

import java.util.List;
import java.util.Set;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;

public class UserService {
    private static final String FETCH_GRAPH_HINT = "jakarta.persistence.fetchgraph";

    private final EntityManagerFactory entityManagerFactory;

    public UserService(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public User createOrUpdate(User user) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            entityManager.getTransaction().begin();
            if (user.isNew()) {
                entityManager.persist(user);
            } else {
                user = entityManager.merge(user);
            }
            entityManager.getTransaction().commit();
        }
        return user;
    }

    public void removeById(long id) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            entityManager.getTransaction().begin();
            User userReference = entityManager.getReference(User.class, id);
            entityManager.remove(userReference);
            entityManager.getTransaction().commit();
        }
    }

    public List<User> findUsersByRole(String role) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            String query = "select r from Role r join fetch r.userList where r.nameId=upper(:role)";
            return entityManager.createQuery(query, Role.class)
                    .setParameter("role", role)
                    .getSingleResult()
                    .getUserList();
        } catch (NoResultException ex) {
            return emptyList();
        }
    }

    public Set<Role> findUserRolesByUserId(long userId) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            EntityGraph<?> entityGraph = entityManager.createEntityGraph(User.USER_WITH_ROLES_ENTITY_GRAPH);
            String query = "select u from User u where u.id = :userId";
            return entityManager.createQuery(query, User.class)
                    .setParameter("userId", userId)
                    .setHint(FETCH_GRAPH_HINT, entityGraph)
                    .getSingleResult()
                    .getRoles();
        } catch (NoResultException ex) {
            return emptySet();
        }
    }

    public Set<Role> findUserRolesByUserName(String userName) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            EntityGraph<User> entityGraph = entityManager.createEntityGraph(User.class);
            entityGraph.addAttributeNodes("roles");
            String query = "select u from User u where u.name = :userName";
            return entityManager.createQuery(query, User.class)
                    .setParameter("userName", userName)
                    .setHint(FETCH_GRAPH_HINT, entityGraph)
                    .getSingleResult()
                    .getRoles();
        } catch (NoResultException ex) {
            return emptySet();
        }
    }
}
