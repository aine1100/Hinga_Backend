package com.Hinga.farmMis.services;

import com.Hinga.farmMis.Model.Farmer;
import com.Hinga.farmMis.repository.FarmerRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class FarmerDetailsService implements UserDetailsService {

    private final FarmerRepository farmerRepository;

    public FarmerDetailsService(FarmerRepository farmerRepository) {
        this.farmerRepository = farmerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Farmer farmer = farmerRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Farmer not found: " + email));

        List<SimpleGrantedAuthority> authorities = (farmer.getRole() != null)
                ? List.of(new SimpleGrantedAuthority(farmer.getRole()))
                : Collections.emptyList();

        return new org.springframework.security.core.userdetails.User(
                farmer.getEmail(),
                farmer.getPassword(),
                authorities
        );
    }
}
