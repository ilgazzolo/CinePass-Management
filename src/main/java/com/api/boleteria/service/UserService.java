package com.api.boleteria.service;

import com.api.boleteria.dto.detail.UserDetailDTO;
import com.api.boleteria.dto.list.BoletoListDTO;
import com.api.boleteria.dto.list.UserListDTO;
import com.api.boleteria.dto.request.RegisterRequestDTO;
import com.api.boleteria.exception.NotFoundException;
import com.api.boleteria.model.Boleto;
import com.api.boleteria.model.Role;
import com.api.boleteria.model.User;
import com.api.boleteria.repository.IBoletoRepository;
import com.api.boleteria.repository.IUserRepository;
import com.api.boleteria.validators.UserValidator;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {

    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final IBoletoRepository boletoRepository;


    ///  registar un usuario
    public UserDetailDTO save (RegisterRequestDTO req){

        UserValidator.CamposValidator(req);
        User entity = new User(
                req.getName(),
                req.getSurname(),
                req.getUsername(),
                req.getEmail(),
                req.getPassword());

        entity.setPassword(passwordEncoder.encode(entity.getPassword()));
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

    /// inicio de sesion
    /* public UserDetailDTO login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadRequestException("Usuario no encontrado"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadRequestException("Credenciales inválidas");
        }


        return new UserDetailDTO(user.getId(), user.getUsername(), user.getRole());
    }

     */


    ///  ver todos los usuarios
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


    ///  ver un usario por id
    public UserDetailDTO findById(Long id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("user ID: "+id+" not found"));
        return new UserDetailDTO(
                user.getId(),
                user.getName(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                user.getRole().name()
        );
    }

    ///  actualizar un usuario
    public UserDetailDTO updateById (Long id, RegisterRequestDTO entity){
        return userRepository.findById(id)
                .map( u -> {
                    u.setName(entity.getName());
                    u.setUsername(entity.getUsername());
                    u.setEmail(entity.getEmail());
                    u.setEmail(entity.getEmail());
                    u.setRole(entity.getRole());
                    u.setPassword(entity.getPassword());

                    User created = userRepository.save(u);

                    return new UserDetailDTO(
                            created.getId(),
                            created.getName(),
                            created.getUsername(),
                            created.getEmail(),
                            created.getPassword(),
                            created.getRole().name()
                    );
                })
                .orElseThrow(() -> new NotFoundException("user ID: "+id+" not found"));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("user not found: "+username));

        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().getRoleName());

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                List.of(authority)
        );
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    ///  visualizar mis entrdas
    public List<BoletoListDTO> visualizarBoletos(Long user_id) {
        /* Obtener usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        Long userId = userDetails.getId()

         */

        // Obtener boletos asociados
        List<Boleto> boletos = boletoRepository.findByUserId(user_id);

        if (boletos.isEmpty()) {
            throw new NotFoundException("No se encontraron boletos para el usuario actual.");
        }

        // Mapear a DTO
        return boletos.stream()
                .map(b -> new BoletoListDTO(
                        b.getId(),
                        b.getFuncion().getId(),
                        b.getFuncion().getMovie().getTitle(),
                        b.getFuncion().getDate(),
                        b.getPrecio()
                ))
                .toList();
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
}
