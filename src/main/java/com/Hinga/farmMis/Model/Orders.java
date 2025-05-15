package com.Hinga.farmMis.Model;

import com.Hinga.farmMis.Constants.OrderStatus;
import com.Hinga.farmMis.Constants.PaymentStatus;
import com.Hinga.farmMis.utils.Address;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Orders {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "order_carts",
        joinColumns = @JoinColumn(name = "order_id"),
        inverseJoinColumns = @JoinColumn(name = "cart_id")
    )
    private java.util.List<Cart> carts;
    private LocalDate orderDate;
    private LocalDate deliveryDate;
    @Embedded
    private Address deliveryAddress;
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Column(name = "buyer_name", nullable = false, length = 255)
    private String buyerName;
    
    @Column(name = "buyer_phone", nullable = false, length = 20)
    private String buyerPhone;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public java.util.List<Cart> getCarts() {
        return carts;
    }

    public void setCarts(java.util.List<Cart> carts) {
        this.carts = carts;
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

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName != null ? buyerName.trim() : null;
    }

    public String getBuyerPhone() {
        return buyerPhone;
    }

    public void setBuyerPhone(String buyerPhone) {
        this.buyerPhone = buyerPhone != null ? buyerPhone.trim() : null;
    }

    @Override
    public String toString() {
        return "Orders{" +
                "id=" + id +
                ", carts=" + carts +
                ", orderDate=" + orderDate +
                ", deliveryDate=" + deliveryDate +
                ", deliveryAddress=" + deliveryAddress +
                ", orderStatus=" + orderStatus +
                ", paymentStatus=" + paymentStatus +
                '}';
    }
}
