package com.cyber.db.entity;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

import static java.util.Collections.unmodifiableSet;

@Entity
@Table(name = "USERS")
@NamedEntityGraph(name = User.USER_WITH_ROLES_ENTITY_GRAPH, attributeNodes = @NamedAttributeNode("roles"))
public class User {
    public static final String USER_WITH_ROLES_ENTITY_GRAPH = "user_with_roles";

    @Id
    @GeneratedValue(generator = "usersSequenceGenerator", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "usersSequenceGenerator", sequenceName = "users_seq", initialValue = 50, allocationSize = 10)
    private long id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToMany
    @JoinTable(name = "USERS_ROLES",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_nameid"))
    private Set<Role> roles = new HashSet<>();

    public User() {
    }

    public boolean isNew() {
        return id == 0;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Role> getRoles() {
        return unmodifiableSet(roles);
    }

    public void clearRoles() {
        this.roles.clear();
    }

    public void addRoles(String... roleNames) {
        for (String roleId : roleNames) {
            Role role = Role.of(roleId);
            roles.add(role);
        }
    }

    public void removeRoles(String... roleNames) {
        for (String roleId : roleNames) {
            Role role = Role.of(roleId);
            roles.remove(role);
        }
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

}
