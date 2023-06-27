package com.cyber.db;

import com.cyber.db.entity.Role;
import com.cyber.db.entity.User;
import com.cyber.db.service.RoleService;
import com.cyber.db.service.UserService;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.Set;

public class Main {

    public static void main(String[] args) {
        try (EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("jpa_example_h2")) {
            UserService userService = new UserService(entityManagerFactory);
            RoleService roleService = new RoleService(entityManagerFactory);

            // Создание нового пользователя
            // ----------------------------
            User newUser1 = new User();
            newUser1.setName("James M. Saunders");
            newUser1.addRoles("read", "write");
            newUser1 = userService.createOrUpdate(newUser1);
            System.out.println("Создан пользователь: " + newUser1 + ", " + newUser1.getRoles());

            // Печать всех пользователей с ролью READ
            // Используется JOIN FETCH
            // --------------------------------------
            System.out.println("Пользователи с ролью 'READ': ");
            userService.findUsersByRole("read")
                    .forEach(System.out::println);

            // Создание новой роли WEB
            // -----------------------
            Role webRole = new Role("web", "Разрешен web доcтуп.");
            roleService.createOrUpdate(webRole);
            System.out.println("Создана новая роль: " + webRole);

            // Обновление пользователя
            // Удаление роли READ. Добавление ролей INFO, STATS, WEB
            // -----------------------------------------------------
            newUser1.removeRoles("read");
            newUser1.addRoles("info", "stats", "web");
            newUser1 = userService.createOrUpdate(newUser1);
            System.out.println("Роли пользователя " + newUser1 + " были изменены.");

            // Вывод ролей пользователя по id
            // Используется NamedEntityGraph
            // ------------------------------
            Set<Role> newUserRoleSet = userService.findUserRolesByUserId(newUser1.getId());
            System.out.println("Текущие роли пользователя " + newUser1 + ": " + newUserRoleSet);

            // Вывод ролей пользователя по имени
            // Используется EntityGraph
            // ---------------------------------
            String userName = "Pierre J. Gouge";
            Set<Role> userRolesByUserName = userService.findUserRolesByUserName(userName);
            System.out.println("Роли пользователя с именем '" + userName + "': " + userRolesByUserName);

            // Удаление пользователя
            // ---------------------
            userService.removeById(newUser1.getId());
            System.out.println("Пользователь удален: " + newUser1);

            // Удаление роли
            // -------------
            roleService.removeById("web");
            System.out.println("Неиспользуемая роль WEB была успешно удалена");
        }
    }

}
