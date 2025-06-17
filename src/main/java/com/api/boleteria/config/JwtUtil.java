package com.api.boleteria.config;


import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

public class JwtUtil {
    // 1) Clave secreta para firmar y validar. En producción usar variables de entorno.
    private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);


    // 2) Tiempo de validez en ms (15-30 min recomendado)
    private static final long EXPIRATION = 3600000L; // 1 hora

    /** Crea un JWT */
    public static String createToken(String username, List<String> roles) {
        return Jwts.builder()
                .setSubject(username)                   // claim "sub"
                .claim("roles", roles)                // claim personalizado
                .setIssuedAt(new Date())                // claim "iat"
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION)) // "exp"
                .signWith(SECRET_KEY)
                .compact();                             // construye el string
    }

    /** Valida firma y expiración */
    public static boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token);  // lanza excepción si inválido o expirado
            return true;
        } catch (ExpiredJwtException e) {
            System.err.println("JWT expirado: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("JWT inválido: " + e.getMessage());
        }
        return false;
    }

    /** Obtiene el sujeto (username) */
    public static String getUsername(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Obtiene lista de roles.
     * @SuppressWarnings para suprimir warning de cast genérico.
     */
    @SuppressWarnings("unchecked")
    public static List<String> getRoles(String token) {
        return (List<String>) Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody()
                .get("roles");
    }
}
