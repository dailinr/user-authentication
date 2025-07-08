package com.dailin.backend.users_app.backend_usersapp.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dailin.backend.users_app.backend_usersapp.repositories.UserRepository;

@Service
public class JpaUserDetailsService implements UserDetailsService{

    @Autowired
    private UserRepository repository;

    // implementaremos nuestra clase de login
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // encontrar si está o no el usuario - true or false
        Optional<com.dailin.backend.users_app.backend_usersapp.models.entities.User> opt = repository.getUserByUsername(username);

        // si no hay un usuario con ese username 
        if(!opt.isPresent()) {
            throw new UsernameNotFoundException("Username "+username+" no existe en el sistema"); // lanzamos una excepcion
        }

        // obtenemos el usuario (metodo con excepcion a errores)
        com.dailin.backend.users_app.backend_usersapp.models.entities.User user = opt.orElseThrow();

        // creamos un listado de roles - obtenidos desde la base de datos
        List<GrantedAuthority> authorities = user.getRoles() // se obtienen los roles del usuario
                .stream() // en vez de un for para recorrer los roles, este devuelve una secuencia de Stream
                .map(r -> new SimpleGrantedAuthority(r.getName())) // por cada rol se crearan nuevas instancias de SimpleGrantendAutority que implementa la interface GrantedAutority
                .collect(Collectors.toList()); // convertir para que sea compatible con el tipo List

        // User de spring security (username, password, habilitado, cuenta_no_expire, credenciales_no_expiren, no_bloqueada, roles)
        return new User(
                user.getUsername(),
                user.getPassword(),
                true, 
                true, 
                true, 
                true, 
                authorities);
    }

}