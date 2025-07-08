package com.dailin.backend.users_app.backend_usersapp.auth;

import javax.crypto.SecretKey;
import io.jsonwebtoken.security.Keys;
// import io.jsonwebtoken.Jwts;
import java.nio.charset.StandardCharsets;

public class TokenJWTConfig {
    
    // confg llave secreta - copiado desde repo github jjwt
    // public final static SecretKey SECRET_KEY = Jwts.SIG.HS256.key().build();

    // **CAMBIO CLAVE AQUÍ:** Usamos una cadena base64 codificada como clave secreta fija.
    // Esta cadena debe ser lo suficientemente larga y compleja.
    // Puedes generar una con un generador de claves base64 online o programáticamente una vez.
    // Ejemplo de una clave de 256 bits (32 bytes) codificada en Base64:
    public final static SecretKey SECRET_KEY = Keys.hmacShaKeyFor("AlgunaClaveSecretaMuyLargaYSeguraParaFirmarMisTokensJWTQueNoSeDebeCompartirConNadie1234567890".getBytes(StandardCharsets.UTF_8));
    // Asegúrate de que la cadena sea lo suficientemente larga para HS256 (mínimo 32 bytes o 256 bits)


    public final static String PREFIX_TOKEN = "Bearer ";

    public final static String HEADER_AUTH = "Authorization";

    
}
