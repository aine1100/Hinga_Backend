package com.Hinga.farmMis.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "farmers")  // Explicit table name
public class Farmer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Use IDENTITY for better DB support
    private Long id;

    private String name;


    @Column(unique = true, nullable = false)  // Ensure email is unique and not null
    private String email;

    private String password;  // Will be hashed before saving

    @OneToMany(mappedBy = "farmer", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore  // Prevents infinite recursion in JSON responses
    private List<Farm> farms;

    // Default Constructor
    public Farmer() {}

    public Farmer(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    // Getters and Setters
    public Long getId() { return id; }


    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }  // Hash before saving

    public List<Farm> getFarms() { return farms; }
    public void setFarms(List<Farm> farms) { this.farms = farms; }
}
