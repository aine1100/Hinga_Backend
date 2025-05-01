package com.Hinga.farmMis.services;

import com.Hinga.farmMis.Model.Farmer;
import com.Hinga.farmMis.Model.Users;
import com.Hinga.farmMis.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class FarmerDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public FarmerDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        Users users = userRepository.findById(Long.valueOf(id));
        if(users == null) {
            throw new UsernameNotFoundException(id + " not found");
        }


        List<SimpleGrantedAuthority> authorities = (users.getUserRole() != null)
                ? List.of(new SimpleGrantedAuthority(users.getUserRole().toString()))
                : Collections.emptyList();

        return new org.springframework.security.core.userdetails.User(
                users.getEmail(),
                users.getPassword(),
                authorities
        );
    }
}
