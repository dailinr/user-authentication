package com.dailin.backend.users_app.backend_usersapp.auth.models.dto.mapper;

import com.dailin.backend.users_app.backend_usersapp.auth.models.dto.UserDto;
import com.dailin.backend.users_app.backend_usersapp.models.entities.User;

public class DtoMapperUser {

    private static DtoMapperUser mapper;

    private User user;
    private DtoMapperUser(){
    }

    public static DtoMapperUser builder(){
        mapper = new DtoMapperUser();
        return mapper;
    }

    public DtoMapperUser setUser(User user) {
        this.user = user;
        return mapper;
    }
    
    public UserDto build() {
        if(user == null){
            throw new RuntimeException("Debe pasar el entity user");
        }
        return new UserDto(this.user.getId(), user.getUsername(), user.getEmail()); // se crea la instancia con los datos del usuario
    }
}
