package com.api.boleteria.service;

import com.api.boleteria.dto.detail.UserDetailDTO;
import com.api.boleteria.dto.list.UserListDTO;
import com.api.boleteria.dto.request.UserRequestDTO;
import com.api.boleteria.exception.NotFoundException;
import com.api.boleteria.model.User;
import com.api.boleteria.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDetailDTO save (User entity){
        entity.setPassword(passwordEncoder.encode(entity.getPassword()));
        User saved = userRepository.save(entity);
        return new UserDetailDTO(
                saved.getId(),
                saved.getName(),
                saved.getSurname(),
                saved.getEmail(),
                saved.getPassword(),
                saved.getRole().name()
        );
    }

    public List<UserListDTO> findAll (){
        return userRepository.findAll()
                .stream()
                .map(u -> new UserListDTO(
                        u.getId(),
                        u.getName(),
                        u.getSurname(),
                        u.getEmail(),
                        u.getRole().name()
                ))
                .toList();
    }

    public UserDetailDTO findByUserName(String username){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("user not found: "+username));
        return new UserDetailDTO(
                user.getId(),
                user.getName(),
                user.getSurname(),
                user.getEmail(),
                user.getPassword(),
                user.getRole().name()
        );
    }

    public UserDetailDTO findById(Long id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("user ID: "+id+" not found"));
        return new UserDetailDTO(
                user.getId(),
                user.getName(),
                user.getSurname(),
                user.getEmail(),
                user.getPassword(),
                user.getRole().name()
        );
    }

    public UserDetailDTO updateById (Long id, UserRequestDTO entity){
        return userRepository.findById(id)
                .map( u -> {
                    u.setName(entity.getName());
                    u.setSurname(entity.getSurname());
                    u.setEmail(entity.getEmail());
                    u.setEmail(entity.getEmail());
                    u.setRole(entity.getRole());
                    u.setPassword(entity.getPassword());

                    User created = userRepository.save(u);

                    return new UserDetailDTO(
                            created.getId(),
                            created.getName(),
                            created.getSurname(),
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
    
}
