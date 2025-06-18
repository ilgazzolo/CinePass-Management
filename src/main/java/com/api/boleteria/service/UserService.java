package com.api.boleteria.service;

import com.api.boleteria.config.JwtUtil;
import com.api.boleteria.dto.detail.UserDetailDTO;
import com.api.boleteria.dto.list.UserListDTO;
import com.api.boleteria.dto.request.LoginRequestDTO;
import com.api.boleteria.dto.request.RegisterRequestDTO;
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

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {

    private final IUserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final ITicketRepository ticketRepository;

    private final AuthenticationManager authManager;

    /**
     * crea un nuevo usuario
     * @param req RegisterRequest del nuevo usuario
     * @returs userDetail
     */
    public UserDetailDTO save (RegisterRequestDTO req){

        UserValidator.CamposValidator(req);
        User entity = new User(
                req.getName(),
                req.getSurname(),
                req.getUsername(),
                req.getEmail(),
                new BCryptPasswordEncoder().encode(req.getPassword()));

        User saved = userRepository.save(entity);
        return new UserDetailDTO(
                saved.getId(),
                saved.getName(),
                saved.getSurname(),
                saved.getUsername(),
                saved.getEmail(),
                saved.getRole().name()
        );
    }


    /**
     * muestra todos los usuarios
     * @return List de UserList
     */
    public List<UserListDTO> findAllUsers (){
        return userRepository.findAll()
                .stream()
                .map(u -> new UserListDTO(
                        u.getId(),
                        u.getUsername(),
                        u.getEmail(),
                        u.getRole().name()
                ))
                .toList();
    }


    /**
     * muestra suarios por id
     * @param id de usuario a mostrar
     * @return UserDetail
     */
    public UserDetailDTO findById(Long id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("El usuario con ID: "+id+" no fue encontrado."));
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
     * muetra usuarios por nombre
     * @param username de usuario a mostrar
     * @return UserDetail
     */
    public UserDetailDTO findByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("El usuario con nombre: "+username+" no fue encontrado"));

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
     * actualizacion de usuario
     * @param req usuario con cambios realizados
     * @return UserDetail
     */
    public UserDetailDTO update(RegisterRequestDTO req) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("El usuario con nombre: "+username+" no fue encontrado."));

        user.setName(req.getName());
        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        user.setRole(req.getRole());
        user.setPassword(new BCryptPasswordEncoder().encode(req.getPassword()));

        User updated = userRepository.save(user);

        return new UserDetailDTO(
                updated.getId(),
                updated.getName(),
                updated.getSurname(),
                updated.getUsername(),
                updated.getEmail(),
                updated.getRole().name()
        );
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("El usuario con nombre: "+username+" no fue encontrado."));

        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().getRoleName());

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                List.of(authority)
        );
    }


    public boolean makeUserAdmin(String username) {
        return userRepository.findByUsername(username)
                .map(user -> {
                    user.setRole(Role.ADMIN);
                    userRepository.save(user);
                    return true;
                })
                .orElse(false);
    }


    public Boolean existsByUsername (String username){
        return userRepository.existsByUsername(username);
    }


    public UserDetailDTO getProfile() {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("El usuario con nombre: "+username+" no fue encontrado."));

        return new UserDetailDTO(
                user.getId(),
                user.getName(),
                user.getSurname(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name()
        );
    }


    public Map<String, String> login(LoginRequestDTO req, AuthenticationManager authManager) {
        // 1) Construir token de autenticación
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
        );

        // 2) Generar JWT si éxito
        UserDetails user = (UserDetails) auth.getPrincipal();
        String jwt = JwtUtil.createToken(
                user.getUsername(),
                user.getAuthorities().stream()
                        .map(a -> a.getAuthority().replace("ROLE_", ""))
                        .collect(Collectors.toList())
        );

        return Map.of("token", jwt);
    }



}