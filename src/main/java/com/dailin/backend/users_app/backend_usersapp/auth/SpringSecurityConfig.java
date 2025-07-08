package com.dailin.backend.users_app.backend_usersapp.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.dailin.backend.users_app.backend_usersapp.auth.filters.JwtAuthenticationFilter;
import com.dailin.backend.users_app.backend_usersapp.auth.filters.JwtValidationfilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// clase de configuracion spring security
@Configuration
public class SpringSecurityConfig {

    @Autowired
    private AuthenticationConfiguration authenticationConfiguration; // componente de Spring

    // metodo passwordEncoder para encriptar nuestra contraseña
    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(); // robusto
    }

    // SecurityFilterChain: bean principal que configura la seguridad de la aplicación.
    @Bean // el valor de retorno del método se registra como un bean 
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception{  // cadena de filtros de seguridad para todas solicitudes HTTP
        // Creamos una instancia del JwtAuthenticationFilter
        // Esto es importante para poder configurar su URL de procesamiento.
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(authenticationConfiguration.getAuthenticationManager());
        jwtAuthenticationFilter.setFilterProcessesUrl("/login"); // Asegúrate de que esta sea la URL a la que envías tu POST de login.

        return http.authorizeHttpRequests(authRequest -> {
            authRequest
                .requestMatchers(HttpMethod.GET, "/users").permitAll() // Ruta pública para listar usuarios
                .requestMatchers(HttpMethod.GET, "/users/{id}").hasAnyRole( "ADMIN") // los usuarios que tengan estos roles pueden acceder a la ruta
                .requestMatchers(HttpMethod.POST, "/users").hasRole("ADMIN") // solo el admin puede crear un usario
                .requestMatchers("/users/**").hasRole("ADMIN") // solo el admin puede crear un usario

                // .requestMatchers(HttpMethod.DELETE, "/users/{id}").hasRole("ADMIN") // solo el admin puede crear un usario
                // .requestMatchers(HttpMethod.PUT, "/users/{id}").hasRole("ADMIN") // solo el admin puede crear un usario
                .anyRequest().authenticated(); // Todas las demás rutas requieren autenticación
            })
            // Añadimos el filtro de autenticación (login)
            .addFilter(jwtAuthenticationFilter)
            // Añadimos el filtro de validación *antes* del filtro estándar de Spring Security
            // para que se procese el token JWT en cada petición autenticada.
            // Si el token es válido, establece el contexto de seguridad y permite el acceso.
            .addFilterBefore(new JwtValidationfilter(authenticationConfiguration.getAuthenticationManager()), UsernamePasswordAuthenticationFilter.class)
            .csrf(config -> config.disable())
            .sessionManagement(managment -> managment.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .build();
    }

    @Bean
    AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
