package com.api.boleteria.config;

import com.api.boleteria.dto.request.RegisterRequestDTO;
import com.api.boleteria.model.enums.Role;
import com.api.boleteria.repository.IUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import com.api.boleteria.model.User;

@Component
public class DataInitializer implements CommandLineRunner {

    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(IUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByUsername("admin").isEmpty()) {
            RegisterRequestDTO adminDto = new RegisterRequestDTO(
                    "Admin",
                    "Admin",
                    "admin",
                    "admin@tuapp.com",
                    "Admin123!"
            );
            adminDto.setRole(Role.ADMIN);

            User admin = new User();
            admin.setName(adminDto.getName());
            admin.setSurname(adminDto.getSurname());
            admin.setUsername(adminDto.getUsername());
            admin.setEmail(adminDto.getEmail());
            admin.setPassword(passwordEncoder.encode(adminDto.getPassword()));
            admin.setRole(adminDto.getRole());

            userRepository.save(admin);
        }
    }
}

