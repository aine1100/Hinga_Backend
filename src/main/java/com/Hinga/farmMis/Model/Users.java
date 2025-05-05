package com.Hinga.farmMis.Model;

import com.Hinga.farmMis.Constants.UserRoles;
import com.Hinga.farmMis.utils.Address;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Users {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
    private String password;
    @Embedded
    private Address address;
    @Enumerated(EnumType.STRING)
    private UserRoles userRole;
    private String resetPasswordToken;
    @OneToMany(mappedBy = "farmer", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Livestock> livestock;
    @OneToMany(mappedBy = "buyer", fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Cart> carts = new ArrayList<>();

    public LocalDateTime getResetPasswordTokenCreatedAt() {
        return resetPasswordTokenCreatedAt;
    }

    public void setResetPasswordTokenCreatedAt(LocalDateTime resetPasswordTokenCreatedAt) {
        this.resetPasswordTokenCreatedAt = resetPasswordTokenCreatedAt;
    }

    @Column
    private LocalDateTime resetPasswordTokenCreatedAt;

    public String getResetPasswordToken() {
        return resetPasswordToken;
    }

    public void setResetPasswordToken(String resetPasswordToken) {
        this.resetPasswordToken = resetPasswordToken;
    }

    public  Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public UserRoles getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRoles userRole) {
        this.userRole = userRole;
    }

    public List<Livestock> getLivestock() {
        return livestock;
    }

    public void setLivestock(List<Livestock> livestock) {
        this.livestock = livestock;
    }

    public List<Cart> getCarts() {
        return carts;
    }

    public void setCarts(List<Cart> carts) {
        this.carts = carts;
    }
}
