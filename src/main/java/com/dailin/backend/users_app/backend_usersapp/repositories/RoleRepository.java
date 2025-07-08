package com.dailin.backend.users_app.backend_usersapp.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.dailin.backend.users_app.backend_usersapp.models.entities.Role;

public interface RoleRepository extends CrudRepository<Role, Long> {
    
    // forma 1: se utiliza la palabra clave findBy para crudRepository
    Optional<Role> findByname(String name);
}
