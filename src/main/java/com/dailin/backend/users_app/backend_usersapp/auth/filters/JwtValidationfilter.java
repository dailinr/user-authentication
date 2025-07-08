package com.dailin.backend.users_app.backend_usersapp.auth.filters;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.dailin.backend.users_app.backend_usersapp.auth.SimpleGrantedAuthorityJsonCreator;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import static com.dailin.backend.users_app.backend_usersapp.auth.TokenJWTConfig.*;

public class JwtValidationfilter extends BasicAuthenticationFilter{

    public JwtValidationfilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        // obtenemos el valor del header
        String header = request.getHeader(HEADER_AUTH);
        
        // validamos que la cabecera authorization contenga bearer
        if(header == null || !header.startsWith(PREFIX_TOKEN)){
            // si no es asi, continuamos con la cadena filtros (req, res) y nos salimos del filtro actual
            chain.doFilter(request, response); // nos llevara a las rutas publicas
            return;
        }
        
        // obtenemos el token solo - eliminando el prefijo Bearer
        String token = header.replace(PREFIX_TOKEN, "");

        try{
            // validamos que el token recoguido de los headers(cliente) 
            Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY) // q tenga la misma firma del token creado en JwtAuthFilter
                .build()
                .parseClaimsJws(token)
                .getBody(); // devolvera el usuario .si algo falla se lanza la excepcion en catch
            
            Object authoritiesClaims = claims.get("authorities"); // obtenemos los roles del usuario desde los claims del token
            String username = claims.getSubject();

            Collection<? extends GrantedAuthority> authorities = Arrays // se buscar convertir un array JSON a un array list<GrantedAuthority>
                .asList(new ObjectMapper() // objectMapper se utiliza para crear o
                    .addMixIn( SimpleGrantedAuthority.class, SimpleGrantedAuthorityJsonCreator.class) // busca solucionar problema de SimpleGrantedAuth para que pase el nombre_role del JSON
                    .readValue(authoritiesClaims.toString().getBytes(), SimpleGrantedAuthority[].class) // se convierete a bytes y luego se poblan cada objeto del array como SimpleGrantedAuthority
                ); 

            // y luego nos autenticamos
            UsernamePasswordAuthenticationToken authentication =  
                new UsernamePasswordAuthenticationToken(username, null, authorities);

            // ya nos logeamos - permitimos al usuario con este token pasara recurso protegido
            SecurityContextHolder.getContext().setAuthentication(authentication);
            chain.doFilter(request, response); // damos pase a los siguientes filtros
        } 
        catch(JwtException e){
            Map<String, String> body = new HashMap<>();

            body.put("error", e.getMessage());
            body.put("message", "El token no es valido o ha expirado.");
            response.getWriter().write(new ObjectMapper().writeValueAsString(body));
            response.setStatus(403);
            response.setContentType("application/json");
        }
    }

}
