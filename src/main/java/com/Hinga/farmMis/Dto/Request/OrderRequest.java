package com.Hinga.farmMis.Dto.Request;

import com.Hinga.farmMis.utils.Address;
import java.time.LocalDate;
import java.util.List;

public class OrderRequest {
    private List<Long> selectedCartIds;
    private LocalDate deliveryDate;
    private Address deliveryAddress;

    // Getters and Setters
    public List<Long> getSelectedCartIds() {
        return selectedCartIds;
    }

    public void setSelectedCartIds(List<Long> selectedCartIds) {
        this.selectedCartIds = selectedCartIds;
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