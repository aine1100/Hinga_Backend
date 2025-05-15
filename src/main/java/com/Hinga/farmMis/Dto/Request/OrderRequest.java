package com.Hinga.farmMis.Dto.Request;

import com.Hinga.farmMis.Model.Cart;
import com.Hinga.farmMis.utils.Address;
import java.time.LocalDate;
import java.util.List;

public class OrderRequest {
    private List<Cart> carts;
    private LocalDate deliveryDate;
    private Address deliveryAddress;

    // Getters and Setters
    public List<Cart> getCarts() {
        return carts;
    }

    public void setCarts(List<Cart> carts) {
        this.carts = carts;
    }

    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(LocalDate deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public Address getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(Address deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }
} 