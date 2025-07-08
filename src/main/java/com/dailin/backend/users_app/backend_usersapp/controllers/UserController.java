package com.dailin.backend.users_app.backend_usersapp.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dailin.backend.users_app.backend_usersapp.auth.models.dto.UserDto;
import com.dailin.backend.users_app.backend_usersapp.models.entities.User;
import com.dailin.backend.users_app.backend_usersapp.services.UserService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService service;

    @GetMapping
    public List<UserDto> list(){
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> show(@PathVariable Long id){
        Optional<UserDto> userOptional = service.findById(id);

        if(userOptional.isPresent()){
            // si el userId está presente devuelve status 200,  con el usuario en el cuerpo de la respuesta
            return ResponseEntity.ok(userOptional.orElseThrow());
        }

        // devuelve un state 404 
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody User user, BindingResult result){
        if(result.hasErrors()){
            return validation(result);
        }

        UserDto userDB = service.save(user);

        // status created para que devuelva un 201, y en el cuerpo de la respuesta el usuario JSON
        return ResponseEntity.status(HttpStatus.CREATED).body(userDB);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@Valid @RequestBody User user, BindingResult result, @PathVariable Long id){
        if(result.hasErrors()){
            return validation(result);
        }

        Optional<UserDto> userOp = service.update(user, id);

        if(userOp.isPresent()){
            return ResponseEntity.status(HttpStatus.CREATED).body(userOp.orElseThrow());
        }

        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> remove(@PathVariable Long id){
        
        Optional<UserDto> userOp = service.findById(id);

        if(userOp.isPresent()){
            service.remove(id);

            // noContent(204) porque no devuelve nada en body
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

    private ResponseEntity<?> validation(BindingResult result) {
        
        // coleccion map que contiene <key, valor> - hasmap la implementa
        Map<String, String> errors = new HashMap<>();

        // devuelve una lista de errores, lo iteramos
        result.getFieldErrors().forEach(err -> {

            // obt el campo de cada err, y asignamos su valor (un cadena de mensaje) - armamos un JSON
            errors.put(err.getField(), "El campo "+err.getField()+" "+err.getDefaultMessage());
        });

        // devolvemos el mapa en el cuerpo de la respuesta (400)
        return ResponseEntity.badRequest().body(errors);
    }
}
