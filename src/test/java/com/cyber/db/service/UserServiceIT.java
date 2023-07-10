package com.cyber.db.service;

import com.cyber.db.entity.Role;
import com.cyber.db.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class UserServiceIT extends AbstractPostgresIT {
    private static String TEST_USER_NAME = "TEST_USER_NAME";
    private static String TEST_USER_ROLE_READ = "READ";
    private static long ID_NOT_EXIST = 999_999_999L;

    static UserService sut;

    @BeforeAll
    static void beforeAll() {
        startContainer();
        sut = new UserService(getEntityManagerFactory());
    }

    @AfterAll
    static void afterAll() {
        sut = null;
        stopContainer();
    }

    private User createNextTestUser() {
        User user = new User();
        user.setName(TEST_USER_NAME + "-" + UUID.randomUUID());
        user.addRoles(TEST_USER_ROLE_READ);
        return user;
    }

    private boolean isUserExist(long id) {
        try (EntityManager em = getEntityManagerFactory().createEntityManager()) {
            return em.find(User.class, id) != null;
        }
    }

    @Test
    void createOrUpdate_create_success() {
        User testUser = createNextTestUser();

        User dbUser = sut.createOrUpdate(testUser);

        assertThat(dbUser.getId(), greaterThan(0L));
        assertThat(dbUser.getName(), equalTo(testUser.getName()));
        assertThat(dbUser.getRoles(), hasItem(equalTo(new Role(TEST_USER_ROLE_READ, ""))));
    }

    @Test
    void createOrUpdate_create_not_unique_name_throws_exception() {
        User testUser1 = sut.createOrUpdate(createNextTestUser());

        User testUser2 = createNextTestUser();
        testUser2.setName(testUser1.getName());

        Assertions.assertThrows(PersistenceException.class, () ->
                sut.createOrUpdate(testUser2)
        );
    }

    @Test
    void createOrUpdate_create_not_exist_role_throws_exception() {
        User testUser = createNextTestUser();
        testUser.addRoles("ROLE_NOT_EXIST");

        Assertions.assertThrows(PersistenceException.class, () ->
                sut.createOrUpdate(testUser)
        );
    }


    @Test
    void createOrUpdate_update_success() {
        String newName = "NEW_USER_NAME";
        String newUserRole = "WRITE";

        User testUser = sut.createOrUpdate(createNextTestUser());
        testUser.setName(newName);
        testUser.addRoles(newUserRole);
        User updatedUser = sut.createOrUpdate(testUser);

        assertThat(updatedUser.getId(), equalTo(testUser.getId()));
        assertThat(updatedUser.getName(), equalTo(newName));
        assertThat(updatedUser.getRoles(), hasItem(equalTo(new Role(TEST_USER_ROLE_READ, ""))));
        assertThat(updatedUser.getRoles(), hasItem(equalTo(new Role(newUserRole, ""))));
    }

    @Test
    void removeById_success() {
        User testUser = sut.createOrUpdate(createNextTestUser());

        boolean existBeforeRemove = isUserExist(testUser.getId());
        sut.removeById(testUser.getId());
        boolean existAfterRemove = isUserExist(testUser.getId());

        assertThat(existBeforeRemove, is(true));
        assertThat(existAfterRemove, is(false));
    }

    @Test
    void removeById_failure() {
        Assertions.assertThrows(PersistenceException.class, () ->
                sut.removeById(ID_NOT_EXIST)
        );
    }

    @Test
    void findUsersByRole_success() {
        User testUser = sut.createOrUpdate(createNextTestUser());

        List<User> usersByRole = sut.findUsersByRole(TEST_USER_ROLE_READ);

        User dbUser = usersByRole.stream()
                .filter(u -> u.getId() == testUser.getId())
                .findAny()
                .get();

        assertThat(dbUser.getName(), equalTo(testUser.getName()));
    }

    @Test
    void findUsersByRole_not_found_failure() {
        String notExistRoleName = "NOT_EXIST_ROLE_NAME";
        User testUser = sut.createOrUpdate(createNextTestUser());

        List<User> usersByRole = sut.findUsersByRole(notExistRoleName);

        assertThat(usersByRole, empty());
    }

    @Test
    void findUserRolesByUserId_success() {
        User testUser = sut.createOrUpdate(createNextTestUser());

        Set<Role> roleSet = sut.findUserRolesByUserId(testUser.getId());

        assertThat(roleSet.containsAll(testUser.getRoles()), is(true));
    }

    @Test
    void findUserRolesByUserId_not_found_failure() {
        Set<Role> userRolesByUserId = sut.findUserRolesByUserId(ID_NOT_EXIST);

        assertThat(userRolesByUserId, empty());
    }

    @Test
    void findUserRolesByUserName_success() {
        User testUser = sut.createOrUpdate(createNextTestUser());

        Set<Role> roleSet = sut.findUserRolesByUserName(testUser.getName());

        assertThat(roleSet.containsAll(testUser.getRoles()), is(true));
    }

    @Test
    void findUserRolesByUserName_not_found_failure() {
        String notExistUserName = "NOT_EXIST_USER_NAME_" + ID_NOT_EXIST;

        Set<Role> userRolesByUserName = sut.findUserRolesByUserName(notExistUserName);

        assertThat(userRolesByUserName, empty());
    }
}