package com.api.boleteria.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration                    // Define clase de configuración
@EnableWebSecurity                // Activar seguridad web
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // 1. CSRF OFF: no hay sesión
                .formLogin(form -> form.disable()) // 2. FormLogin OFF
                .httpBasic(basic -> basic.disable()) // 3. HTTP Basic OFF

                .sessionManagement(sm -> // 4. Gestión de sesiones
                        sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/cinema/**").hasAnyRole("ADMIN", "CLIENT")
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/client/**").hasRole("CLIENT")
                        .requestMatchers("/api/auth/**").permitAll()
                        .anyRequest().authenticated())

                .addFilterBefore(jwtAuthFilter(), // 5. Insertar filtro JWT
                        UsernamePasswordAuthenticationFilter.class);

        return http.build(); // Construir cadena de filtros
    }

    @Bean
    public JwtAuthFilter jwtAuthFilter() {
        return new JwtAuthFilter(); // Instancia de nuestro filtro personalizado
    }


    // Necesario si más adelante se usa AuthenticationManager (opcional por ahora)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }


}
