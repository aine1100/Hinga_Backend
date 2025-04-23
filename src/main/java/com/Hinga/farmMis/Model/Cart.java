package com.Hinga.farmMis.Model;

import com.Hinga.farmMis.Constants.ProductCategory;
import com.Hinga.farmMis.Constants.ProductUnits;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table( name = "cart")
@NoArgsConstructor
@AllArgsConstructor

public class Cart {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String productName;
    private int quantity;
    private double price;
    @Enumerated(EnumType.STRING)
    private ProductCategory productCategory;
    @Enumerated(EnumType.STRING)
    private ProductUnits productUnits;
    private String owner_email;
    private String description;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public ProductCategory getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(ProductCategory productCategory) {
        this.productCategory = productCategory;
    }

    public ProductUnits getProductUnits() {
        return productUnits;
    }

    public void setProductUnits(ProductUnits productUnits) {
        this.productUnits = productUnits;
    }

    public String getOwner_email() {
        return owner_email;
    }

    public void setOwner_email(String owner_email) {
        this.owner_email = owner_email;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
