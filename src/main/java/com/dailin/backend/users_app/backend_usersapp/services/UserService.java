package com.dailin.backend.users_app.backend_usersapp.services;

import java.util.List;
import java.util.Optional;

import com.dailin.backend.users_app.backend_usersapp.auth.models.dto.UserDto;
import com.dailin.backend.users_app.backend_usersapp.models.entities.User;

public interface UserService {

    // obtener un listado de todos los usuarios
    List<UserDto> findAll();

    // obtener un usuario según su id
    Optional<UserDto> findById(Long id);

    // devuelve un User y su parametro userJSON (insertar o actualizar)
    UserDto save(User user);

    Optional<UserDto> update(User user, Long id);

    // Elimina un usuario segun su id
    void remove(Long id); 
}