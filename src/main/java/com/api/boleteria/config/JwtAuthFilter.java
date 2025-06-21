package com.api.boleteria.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class JwtAuthFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        // Permite la solicitud pasar si no hay token (para rutas públicas)
        if (header == null || !header.startsWith("Bearer ")) {
            System.out.println("DEBUG JWT Filter: No JWT token or invalid format. Path: " + request.getRequestURI());
            chain.doFilter(request, response);
            return;
        }

        String token = header.substring(7); // Extrae el token "Bearer "

        try {
            if (JwtUtil.validateToken(token)) {
                String username = JwtUtil.getUsername(token);
                List<String> roles = JwtUtil.getRoles(token);

                // DEBUG: Muestra los roles ANTES de añadir el prefijo
                System.out.println("DEBUG JWT Filter: Roles from JWT for " + username + " (before prefix): " + roles);

                List<SimpleGrantedAuthority> authorities = roles.stream()
                        .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                        .collect(Collectors.toList());

                // DEBUG: Muestra las autoridades DESPUÉS de añadir el prefijo
                System.out.println("DEBUG JWT Filter: Authorities for " + username + " (after prefix): " + authorities);

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                username, null, authorities);

                SecurityContextHolder.getContext().setAuthentication(authToken);
                System.out.println("DEBUG JWT Filter: Authentication set for user: " + username + " with authorities: " + authorities);

            } else {
                System.out.println("DEBUG JWT Filter: JWT token is invalid for path: " + request.getRequestURI());
            }
        } catch (Exception e) {
            // Esto captura expiración y otros errores de JWT
            System.err.println("ERROR JWT Filter: Invalid or expired token for path " + request.getRequestURI() + ": " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token inválido o expirado");
            return;
        }

        chain.doFilter(request, response);
    }
}