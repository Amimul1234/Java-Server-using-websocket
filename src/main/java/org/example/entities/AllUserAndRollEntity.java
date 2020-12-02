package org.example.entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class AllUserAndRollEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    @Column(columnDefinition = "LONGTEXT")
    private String image;
    private String role;
    @Column(columnDefinition = "LONGTEXT")
    private String password;

    public AllUserAndRollEntity() {
    }

    public AllUserAndRollEntity(int id, String name, String image, String role, String password) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.role = role;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

