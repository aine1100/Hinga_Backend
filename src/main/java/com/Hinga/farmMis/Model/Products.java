package com.Hinga.farmMis.Model;

import com.Hinga.farmMis.Constants.ProductCategory;
import com.Hinga.farmMis.Constants.ProductUnits;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
//
//@Entity
//@Table
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
public class Products {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private int id;
    private String name;
    private String description;
    private double price;
    @Enumerated(EnumType.STRING)
    private ProductUnits unit;
    @Enumerated(EnumType.STRING)
    private ProductCategory category;
    private String image;
    private String email;


    public String getOwner_email() {
        return email;
    }

    public void setOwner_email(String owner_email) {
        this.email = owner_email;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public ProductUnits getUnit() {
        return unit;
    }

    public void setUnit(ProductUnits unit) {
        this.unit = unit;
    }

    public ProductCategory getCategory() {
        return category;
    }

    public void setCategory(ProductCategory category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "Products{" +

                "id=" + id +
                ",owner=" + email +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", unit=" + unit +
                ", category=" + category +
                ", image='" + image + '\'' +
                '}';
    }
}
