package com.Hinga.farmMis.Dto.Request;

import com.Hinga.farmMis.utils.Address;
import java.util.List;

public class MultiCartOrderRequest {
    private List<Long> cartIds;
    private Address deliveryAddress;

    public List<Long> getCartIds() {
        return cartIds;
    }

    public void setCartIds(List<Long> cartIds) {
        this.cartIds = cartIds;
    }

    public Address getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(Address deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }
} 