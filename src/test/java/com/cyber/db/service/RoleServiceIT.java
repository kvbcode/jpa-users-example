package com.cyber.db.service;

import com.cyber.db.entity.Role;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoleServiceIT extends AbstractPostgresIT {

    static RoleService sut;

    @BeforeAll
    static void beforeAll() {
        startContainer();
        sut = new RoleService(getEntityManagerFactory());
    }

    @AfterAll
    static void afterAll() {
        sut = null;
        stopContainer();
    }

    private boolean isRoleExists(String nameId) {
        try (EntityManager em = getEntityManagerFactory().createEntityManager()) {
            return em.find(Role.class, nameId) != null;
        }
    }

    @Test
    void create_role_success() {
        Role role = sut.createOrUpdate(new Role("ROLE_TEST", "Test role"));
        assertTrue(isRoleExists(role.getNameId()));
    }

    @Test
    void update_role_success() {
        String roleId = "ROLE_TEST";
        String oldDescription = "old Description";
        String newDescription = "new Description";

        sut.createOrUpdate(new Role(roleId, oldDescription));
        sut.createOrUpdate(new Role(roleId, newDescription));

        try (EntityManager em = getEntityManagerFactory().createEntityManager()) {
            Role dbRole = em.find(Role.class, roleId);
            assertEquals(newDescription, dbRole.getDescription());
        }
    }

    @Test
    void remove_role_success() {
        String roleId = "ROLE_TEST";
        Role role = sut.createOrUpdate(new Role(roleId, "Test role"));

        assertTrue(isRoleExists(roleId));
        sut.removeById(roleId);
        assertFalse(isRoleExists(roleId));
    }

    @Test
    void remove_role_not_exist_throws_exception() {
        Assertions.assertThrows(PersistenceException.class, () ->
                sut.removeById("ROLE_NOT_EXIST")
        );
    }
}