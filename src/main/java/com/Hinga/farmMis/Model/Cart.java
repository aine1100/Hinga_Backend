package com.Hinga.farmMis.Model;

import com.Hinga.farmMis.Constants.ProductCategory;
import com.Hinga.farmMis.Constants.ProductUnits;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table( name = "cart")
@NoArgsConstructor
@AllArgsConstructor

public class Cart {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "livestock_id")
    @JsonIgnoreProperties({"carts","farmer"})
    private Livestock livestock;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="buyer_id")
    @JsonIgnoreProperties({"carts","livestock"})
    @JsonIgnore
    private Users buyer;
    private Long quantity;
    private Long unitPrice;
    private Long totalPrice;
    @ManyToMany(mappedBy = "carts", fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Orders> orders = new ArrayList<>();

    private boolean ordered = false;

    public void calculateTotalPrice() {
        if (unitPrice != null && quantity != null) {
            this.totalPrice = unitPrice * quantity;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Livestock getLivestock() {
        return livestock;
    }

    public void setLivestock(Livestock livestock) {
        this.livestock = livestock;
    }

    public Users getBuyer() {
        return buyer;
    }

    public void setBuyer(Users buyer) {
        this.buyer = buyer;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public Long getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Long unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Long getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Long totalPrice) {
        this.totalPrice = totalPrice;
    }

    public List<Orders> getOrders() {
        return orders;
    }

    public void setOrders(List<Orders> orders) {
        this.orders = orders;
    }

    public boolean isOrdered() {
        return ordered;
    }

    public void setOrdered(boolean ordered) {
        this.ordered = ordered;
    }
}
