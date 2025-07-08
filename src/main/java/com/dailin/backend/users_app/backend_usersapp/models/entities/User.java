package com.dailin.backend.users_app.backend_usersapp.models.entities;

import java.util.List;

import org.hibernate.annotations.ManyToAny;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name="users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank // no vacio y sin espacios en blanco
    @Size(min = 4, max = 12) // min y max de caracteres
    @Column(unique = true)
    private String username;

    @NotBlank
    private String password;
    
    @NotBlank
    @Email
    @Column(unique = true)
    private String email;

    // un usuario puede tener varios roles y un rol puede corresponder a varios usuarios
    @ManyToAny // relacion de muchos a muchos
    @JoinTable( // tabla intermedia con idForgein 
        name = "users_roles", 
        joinColumns = @JoinColumn(name="user_id"), // primera llave foranea (id de la tabla user)
        inverseJoinColumns = @JoinColumn(name = "role_id"),  // segunda lla     ve (id de la tabla role)
        uniqueConstraints = { // el conjunto de llaves sea unique, un usuario no puede tener el mismo rol repetido
            @UniqueConstraint(columnNames = {"user_id", "role_id"})
        })
    private List<Role> roles; // es unidireccional: la entidad user contiene roles, pero no es necesario que Role contenga Users


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    
}
