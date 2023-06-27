package com.cyber.db.entity;

import jakarta.persistence.*;

import java.util.Collections;
import java.util.List;

import static java.util.Collections.unmodifiableList;

@Entity
@Table(name = "ROLES")
public class Role {

    @Id
    private String nameId;

    @Column(name = "description", nullable = false)
    private String description = "";

    @ManyToMany(mappedBy = "roles")
    private List<User> userList = Collections.emptyList();

    public Role() {
    }

    public Role(String nameId, String description) {
        this.nameId = nameId.toUpperCase();
        this.description = description;
    }

    public static Role of(String nameId) {
        return new Role(nameId, "");
    }

    public String getNameId() {
        return nameId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<User> getUserList() {
        return unmodifiableList(userList);
    }

    @Override
    public String toString() {
        return "Role{" + nameId + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Role role = (Role) o;

        return nameId.equals(role.nameId);
    }

    @Override
    public int hashCode() {
        return nameId.hashCode();
    }
}
