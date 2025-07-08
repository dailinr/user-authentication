package com.dailin.backend.users_app.backend_usersapp.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.dailin.backend.users_app.backend_usersapp.models.entities.User;

public interface UserRepository extends CrudRepository<User, Long> {
    
    // forma 1: se utiliza la palabra clave findBy para crudRepository
    Optional<User> findByUsername(String username);

    // forma 2: de forma manual consulta SQL
    // obtener todos los campos de la tabla User(u) donde el username del usuario sea igual al valor del primer parametro del metodo
    @Query("select u from User u where u.username=?1")
    Optional<User> getUserByUsername(String username);
}
