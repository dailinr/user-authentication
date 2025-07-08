package com.dailin.backend.users_app.backend_usersapp.auth.filters;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.dailin.backend.users_app.backend_usersapp.models.entities.User;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

// import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import static com.dailin.backend.users_app.backend_usersapp.auth.TokenJWTConfig.*;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// maneja un filtro por de bajo y añade una ruta url "login"
// funciona como una especie de controlador, se invocará cuando halle un metodo POST y cuando la ruta sea "login"
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter{

    // instancia de authentication manager
    private AuthenticationManager authenticationManager;

    // constructor de la clase
    public JwtAuthenticationFilter(AuthenticationManager authenticationManager){
        this.authenticationManager = authenticationManager;
    }

    // metodo para realizar-intentar la autenticacion
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        User user = null ; // por defecto es null
        String username = null;
        String password = null;

        // poblar el objeto de forma manual (leemos del request el body, se guarda dentro de )
        try {
            user = new ObjectMapper().readValue(request.getInputStream(), User.class);
            username = user.getUsername();
            password = user.getPassword();

            // mensaje desde la consola pora login - solo para pruebas
            logger.info("Username desde request InputStream (raw JSON)"+username);
            logger.info("Password desde request InputStream (raw JSON)"+password);

        } // manejo de excepciones para los tres
        catch (StreamReadException e) {  
            e.printStackTrace();
        } catch (DatabindException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // ahora nos vamos autenticar (instanciamos)
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);
        
        logger.info("token: "+authToken);

        // authenticationManager se encarga de autenticar
        return authenticationManager.authenticate(authToken);
    }

    // si se realiza la autenticacion con exito
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
            Authentication authResult) throws IOException, ServletException {

        // obtenemos el username, se hace casth del getPrincipal() al User del package de spring security 
        String username = ((org.springframework.security.core.userdetails.User) authResult.getPrincipal())
            .getUsername();

        Collection<? extends GrantedAuthority> roles = authResult.getAuthorities();
        
        // validar si el rol es admin, anyMatch devuelve un boolean
        boolean isAdmin = roles.stream().anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"));

        // los claims son los datos que se envian en el token
        Map<String,Object> claims = new HashMap<>();
        claims.put("authorities", new ObjectMapper().writeValueAsString(roles)); // se pasan los roles, antes se convierten en obj JSON
        claims.put("isAdmin", isAdmin);

        // cgeneramos un token en formato jwt
        String token = Jwts.builder()
            .setClaims(claims) // se agregan los nuevos claims con los roles y si es admin
            .setSubject(username) // payload - data (informacion no sensible)
            .signWith(SECRET_KEY) // firma con la palabra secreta
            .setIssuedAt(new Date()) // fecha de creacion
            .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // fecha de expiracion(en una hora) 
            .compact(); 
                
        // se da una respuesta al usuario con el token.
        //  tambien pasamos el token en los headers
        response.addHeader(HEADER_AUTH, PREFIX_TOKEN+ token);

        // creamos un Map para el cuerpo de la respuesta
        Map<String, Object> body = new HashMap<>();
        body.put("token", token);
        body.put("message", String.format("Hola %s, has iniciado sesion con exito!!", username));
        body.put("username", username);

        // guardamos en el cuerpo de la respuesta, primero lo convertimos de map a json
        response.getWriter().write(new ObjectMapper().writeValueAsString(body));
        response.setStatus(200);
        response.setContentType("application/json");

    }

    // en caso de que haya un error al autenticar
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException failed) throws IOException, ServletException {
        
        // creamos un map para el cuerpo de la respuesta
        Map<String, Object> body = new HashMap<>();
        
        body.put("message", "ERROR en la autenticacion, username o password incorrecto");
        body.put("error", failed.getMessage()); // informacion del error, se extrae desde la excepcion

        // convertimos a json y guardamos en el cuerpo de la respuesta
        response.getWriter().write(new ObjectMapper().writeValueAsString(body));
        response.setStatus(401); // no authorized
        response.setContentType("application/json");
    }

}
