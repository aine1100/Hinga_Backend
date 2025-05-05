package com.Hinga.farmMis.Model;

import com.Hinga.farmMis.Constants.OrderStatus;
import com.Hinga.farmMis.utils.Address;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Orders {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnore
    @JoinColumn(name = "cart_id",nullable = false)

    private Cart cart;
    private LocalDate orderDate;
    private LocalDate deliveryDate;
    @Embedded
    private Address deliveryAddress;
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
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

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    @Override
    public String toString() {
        return "Orders{" +
                "id=" + id +
                ", cart=" + cart +
                ", orderDate=" + orderDate +
                ", deliveryDate=" + deliveryDate +
                ", deliveryAddress=" + deliveryAddress +
                ", orderStatus=" + orderStatus +
                '}';
    }
}
