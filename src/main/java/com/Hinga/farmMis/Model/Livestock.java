package com.Hinga.farmMis.Model;

import com.Hinga.farmMis.Constants.LivestockStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table
@Entity
public class Livestock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long livestockId;

    private String type;
    private int count;
    private String description;
    private String breed;
    private LocalDate birthDate;
    private double price;
    private int quantity;
    private double weight;

    @Enumerated(EnumType.STRING)
    private LivestockStatus status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "farmer_id")
    @JsonIgnore
    private Users farmer;
    private String imageUrls;

    @OneToMany(mappedBy = "livestock", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Cart> carts = new ArrayList<>();



    public Long getLivestockId() {
        return livestockId;
    }

    public void setLivestockId(Long livestockId) {
        this.livestockId = livestockId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public LivestockStatus getStatus() {
        return status;
    }

    public void setStatus(LivestockStatus status) {
        this.status = status;
    }

    public Users getFarmer() {
        return farmer;
    }

    public void setFarmer(Users farmer) {
        this.farmer = farmer;
    }

    public String getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(String imageUrls) {
        this.imageUrls = imageUrls;
    }

    public List<Cart> getCarts() {
        return carts;
    }

    public void setCarts(List<Cart> carts) {
        this.carts = carts;
    }
}