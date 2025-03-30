package com.Hinga.farmMis.Details;

import com.Hinga.farmMis.Model.Farmer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.List;
import java.util.Collection;

public class FarmerDetails implements UserDetails {

    private final String farmerName;
    private final String password;
    private final List<GrantedAuthority> authorities;

    public FarmerDetails(Farmer farmer) {
        this.farmerName = farmer.getEmail();
        this.password = farmer.getPassword();
        this.authorities = List.of(new SimpleGrantedAuthority(farmer.getRole()));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return farmerName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
