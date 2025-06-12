package com.api.boleteria.service;

import com.api.boleteria.dto.detail.UserDetailDTO;
import com.api.boleteria.dto.list.UserListDTO;
import com.api.boleteria.dto.request.UserRequestDTO;
import com.api.boleteria.exception.NotFoundException;
import com.api.boleteria.model.User;
import com.api.boleteria.repository.IUserRepository;
import lombok.AllArgsConstructor;
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


    public UserDetailDTO save (UserRequestDTO req){

        User entity = new User(
                req.getName(),
                req.getUsername(),
                req.getEmail(),
                req.getPassword(),
                req.getRole());

        entity.setPassword(passwordEncoder.encode(entity.getPassword()));
        User saved = userRepository.save(entity);
        return new UserDetailDTO(
                saved.getId(),
                saved.getName(),
                saved.getUsername(),
                saved.getEmail(),
                saved.getPassword(),
                saved.getRole().name()
        );
    }


    public List<UserListDTO> findAllUsers (){
        return userRepository.findAll()
                .stream()
                .map(u -> new UserListDTO(
                        u.getId(),
                        u.getName(),
                        u.getUsername(),
                        u.getEmail(),
                        u.getRole().name()
                ))
                .toList();
    }


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

    public UserDetailDTO updateById (Long id, UserRequestDTO entity){
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
        return new org.springframework.security.core.userdetails.User(
                user.getName(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority(user.getRole().getRoleName()))
        );
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
}
