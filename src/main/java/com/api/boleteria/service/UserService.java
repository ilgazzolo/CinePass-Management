package com.api.boleteria.service;

import com.api.boleteria.config.JwtUtil;
import com.api.boleteria.dto.detail.UserDetailDTO;
import com.api.boleteria.dto.list.UserListDTO;
import com.api.boleteria.dto.request.LoginRequestDTO;
import com.api.boleteria.dto.request.RegisterRequestDTO;
import com.api.boleteria.exception.BadRequestException;
import com.api.boleteria.exception.NotFoundException;
import com.api.boleteria.model.Role;
import com.api.boleteria.model.User;
import com.api.boleteria.repository.ITicketRepository;
import com.api.boleteria.repository.IUserRepository;
import com.api.boleteria.validators.UserValidator;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Servicio para gestionar operaciones relacionadas con Usuarios.
 */
@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {

    private final IUserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final ITicketRepository ticketRepository;

    /**
     * Crea un nuevo usuario.
     *
     * @param req DTO con la información del nuevo usuario.
     * @return UserDetailDTO con los datos del usuario creado.
     */
    public UserDetailDTO save(RegisterRequestDTO req) {
        UserValidator.validateFields(req);

        if (userRepository.findByEmail(req.getEmail()).isPresent()) {
            throw new BadRequestException("El email ya está registrado.");
        }

        if (userRepository.findByUsername(req.getUsername()).isPresent()) {
            throw new BadRequestException("El nombre de usuario ya está en uso.");
        }

        User entity = new User(
                req.getName(),
                req.getSurname(),
                req.getUsername(),
                req.getEmail(),
                passwordEncoder.encode(req.getPassword())
        );

        User saved = userRepository.save(entity);
        return mapToDetailDTO(saved);
    }

    /**
     * Muestra todos los usuarios.
     *
     * @return Lista de UserListDTO con la información de los usuarios guardados.
     */
    public List<UserListDTO> findAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToListDTO)
                .toList();
    }

    /**
     * Muestra usuario por id.
     *
     * @param id ID de usuario a mostrar.
     * @return UserDetailDTO con la información del usuario encontrado.
     */
    public UserDetailDTO findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("El usuario con ID: " + id + " no fue encontrado."));
        return mapToDetailDTO(user);
    }

    /**
     * Muestra usuario por nombre de usuario.
     *
     * @param username Nombre de usuario a mostrar.
     * @return UserDetailDTO con la información del usuario encontrado.
     */
    public UserDetailDTO findByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("El usuario con nombre: " + username + " no fue encontrado"));
        return mapToDetailDTO(user);
    }

    /**
     * Actualización de usuario.
     *
     * @param req DTO del usuario con cambios realizados.
     * @return UserDetailDTO con la información actualizada del usuario especificado.
     */
    public UserDetailDTO update(RegisterRequestDTO req) {
        User user = getAuthenticatedUser();
        UserValidator.validateFields(req);

        user.setName(req.getName());
        user.setSurname(req.getSurname());
        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        user.setRole(req.getRole());
        user.setPassword(passwordEncoder.encode(req.getPassword()));

        User updated = userRepository.save(user);
        return mapToDetailDTO(updated);
    }

    /**
     * Carga los detalles de un usuario a partir de su nombre de usuario.
     * Este método es utilizado por Spring Security durante el proceso de autenticación.
     *
     * @param username Nombre de usuario.
     * @return UserDetails con la información del usuario autenticado.
     * @throws NotFoundException si el usuario no existe.
     */
    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("El usuario con nombre: " + username + " no fue encontrado."));

        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().getRoleName());

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                List.of(authority)
        );
    }

    /**
     * Cambia el rol de un usuario a ADMIN si el usuario existe.
     *
     * @param username Nombre de usuario a modificar.
     * @return true si el usuario fue encontrado y su rol fue cambiado; false si no se encontró.
     */
    public boolean makeUserAdmin(String username) {
        return userRepository.findByUsername(username)
                .map(user -> {
                    user.setRole(Role.ADMIN);
                    userRepository.save(user);
                    return true;
                })
                .orElse(false);
    }

    /**
     * Verifica si ya existe un usuario con el username especificado.
     *
     * @param username Nombre de usuario a verificar.
     * @return true si el username ya está registrado, false en caso contrario.
     */
    public Boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Obtiene los datos del perfil del usuario autenticado actualmente.
     *
     * @return DTO con los datos del usuario (nombre, apellido, username, email, rol).
     * @throws NotFoundException si el usuario autenticado no existe en la base de datos.
     */
    public UserDetailDTO getProfile() {
        User user = getAuthenticatedUser();
        return mapToDetailDTO(user);
    }

    /**
     * Realiza el proceso de login autenticando al usuario y generando un token JWT si las credenciales son válidas.
     *
     * @param req DTO con los datos de login (username y password).
     * @param authManager AuthenticationManager configurado por Spring Security.
     * @return Mapa con el token JWT generado.
     */
    public Map<String, String> login(LoginRequestDTO req, AuthenticationManager authManager) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
        );

        UserDetails user = (UserDetails) auth.getPrincipal();
        String jwt = JwtUtil.createToken(
                user.getUsername(),
                user.getAuthorities().stream()
                        .map(a -> a.getAuthority().replace("ROLE_", ""))
                        .collect(Collectors.toList())
        );

        return Map.of("token", jwt);
    }

    /**
     * Obtiene el usuario actualmente autenticado en el sistema.
     *
     * @return Entidad User del usuario autenticado.
     * @throws NotFoundException si el usuario no existe.
     */
    public User getAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Usuario con nombre de usuario: " + username + " no encontrado."));
    }

    /**
     * Mapea una entidad User a su DTO detallado.
     *
     * @param user Entidad User a mapear.
     * @return UserDetailDTO con la información del usuario.
     */
    private UserDetailDTO mapToDetailDTO(User user) {
        return new UserDetailDTO(
                user.getId(),
                user.getName(),
                user.getSurname(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name()
        );
    }

    /**
     * Mapea una entidad User a su DTO de lista.
     *
     * @param user Entidad User a mapear.
     * @return UserListDTO con la información básica del usuario.
     */
    private UserListDTO mapToListDTO(User user) {
        return new UserListDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name()
        );
    }

}
