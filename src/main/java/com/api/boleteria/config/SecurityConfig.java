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



// ... tus imports ...

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())

                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        // *** ¡ESTOS ARCHIVOS HTML DEBEN SER PÚBLICOS! ***
                        .requestMatchers(
                                "/",
                                "/index.html",
                                "/admin_dashboard.html", // <--- ¡AQUÍ!
                                "/user_dashboard.html",  // <--- ¡AQUÍ!
                                "/movies_management.html",  // <--- ¡AQUÍ!
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/favicon.ico"
                        ).permitAll()
                        // *************************************************

                        // Permite acceso a los endpoints de autenticación (login y register) sin autenticación
                        .requestMatchers("/api/auth/**").permitAll()

                        // Todas las demás rutas de la API requieren autenticación
                        .requestMatchers("/api/cinemas/**").authenticated()
                        .requestMatchers("/api/movies/**").authenticated()
                        .requestMatchers("/api/functions/**").authenticated()
                        .requestMatchers("/api/card/**").authenticated()
                        .requestMatchers("/api/tickets/**").authenticated()
                        .requestMatchers("/api/userManagement/**").authenticated()
                        .requestMatchers("/api/create_movie").authenticated()

                        // Asegura que cualquier otra petición no especificada también requiera autenticación
                        .anyRequest().authenticated())

                .addFilterBefore(jwtAuthFilter(),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public JwtAuthFilter jwtAuthFilter() {
        return new JwtAuthFilter();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}