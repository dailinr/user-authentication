package com.dailin.backend.users_app.backend_usersapp.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dailin.backend.users_app.backend_usersapp.models.entities.Role;
import com.dailin.backend.users_app.backend_usersapp.models.entities.User;
import com.dailin.backend.users_app.backend_usersapp.repositories.RoleRepository;
import com.dailin.backend.users_app.backend_usersapp.repositories.UserRepository;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository repository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        
        return (List<User>) repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
    
        return repository.findById(id);
    }

    @Override
    @Transactional
    public void remove(Long id) {

        repository.deleteById(id);
    }

    @Override
    @Transactional
    public Optional<User> update(User user, Long id) {

        Optional<User> userOp = this.findById(id);
        // otra forma: userOptional = null;

        if(userOp.isPresent()){
            User userDB = userOp.orElseThrow(); // funciona como un get()

            // en el campo [username] le seteamos el valor desde user enviado (requestBody)
            userDB.setUsername(user.getUsername());
            userDB.setEmail(user.getEmail());

            return Optional.of(this.save(userDB));
            // userOptional = this.save(userDB);
        }
        
        // return Optional.ofNullable(userOptional);
        return Optional.empty();
    }

    @Override
    @Transactional
    public User save(User user) {
        // pasamos la clave de texto plano a encriptarla (la contraseña viene desde el front - cliente)
        String passwordBCrypt = passwordEncoder.encode(user.getPassword());

        // guardamos el password encriptado en el usuario
        user.setPassword(passwordBCrypt);

        // buscar el rol "ROLE_USER"
        Optional<Role> o = roleRepository.findByname("ROLE_USER");
        List<Role> roles = new ArrayList<>();

        // validamos que el rol exista
        if(o.isPresent()){
            // se agrega un rol por defecto (usuario)
            roles.add(o.orElseThrow());
        }

        // se pasan los roles al usuario
        user.setRoles(roles);

        // guardamos el usuario en la base de datos
        return repository.save(user);
    }
}