package com.Hinga.farmMis.Dto.response;

import com.Hinga.farmMis.Constants.OrderStatus;
import com.Hinga.farmMis.Constants.PaymentStatus;

import java.util.Map;
import java.util.List;

public class FarmerOrderSummary {
    private Long totalOrders;
    private Long totalLivestockOrdered;
    private Map<OrderStatus, Long> ordersByStatus;
    private Map<PaymentStatus, Long> ordersByPaymentStatus;
    private Map<String, Long> livestockTypeCounts; // e.g., "Cow" -> 5, "Goat" -> 3
    private List<OrderWithBuyerInfo> ordersWithBuyerInfo; // New field for orders with buyer details

    public FarmerOrderSummary() {
    }

    public Long getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(Long totalOrders) {
        this.totalOrders = totalOrders;
    }

    public Long getTotalLivestockOrdered() {
        return totalLivestockOrdered;
    }

    public void setTotalLivestockOrdered(Long totalLivestockOrdered) {
        this.totalLivestockOrdered = totalLivestockOrdered;
    }

    public Map<OrderStatus, Long> getOrdersByStatus() {
        return ordersByStatus;
    }

    public void setOrdersByStatus(Map<OrderStatus, Long> ordersByStatus) {
        this.ordersByStatus = ordersByStatus;
    }

    public Map<PaymentStatus, Long> getOrdersByPaymentStatus() {
        return ordersByPaymentStatus;
    }

    public void setOrdersByPaymentStatus(Map<PaymentStatus, Long> ordersByPaymentStatus) {
        this.ordersByPaymentStatus = ordersByPaymentStatus;
    }

    public Map<String, Long> getLivestockTypeCounts() {
        return livestockTypeCounts;
    }

    public void setLivestockTypeCounts(Map<String, Long> livestockTypeCounts) {
        this.livestockTypeCounts = livestockTypeCounts;
    }

    public List<OrderWithBuyerInfo> getOrdersWithBuyerInfo() {
        return ordersWithBuyerInfo;
    }

    public void setOrdersWithBuyerInfo(List<OrderWithBuyerInfo> ordersWithBuyerInfo) {
        this.ordersWithBuyerInfo = ordersWithBuyerInfo;
    }

    // Inner class to hold order and buyer information
    public static class OrderWithBuyerInfo {
        private Long orderId;
        private String buyerName;
        private String buyerPhone;
        private String buyerEmail;
        private OrderStatus orderStatus;
        private String deliveryAddress;

        public OrderWithBuyerInfo() {
        }

        public Long getOrderId() {
            return orderId;
        }

        public void setOrderId(Long orderId) {
            this.orderId = orderId;
        }

        public String getBuyerName() {
            return buyerName;
        }

        public void setBuyerName(String buyerName) {
            this.buyerName = buyerName;
        }

        public String getBuyerPhone() {
            return buyerPhone;
        }

        public void setBuyerPhone(String buyerPhone) {
            this.buyerPhone = buyerPhone;
        }

        public String getBuyerEmail() {
            return buyerEmail;
        }

        public void setBuyerEmail(String buyerEmail) {
            this.buyerEmail = buyerEmail;
        }

        public OrderStatus getOrderStatus() {
            return orderStatus;
        }

        public void setOrderStatus(OrderStatus orderStatus) {
            this.orderStatus = orderStatus;
        }

        public String getDeliveryAddress() {
            return deliveryAddress;
        }

        public void setDeliveryAddress(String deliveryAddress) {
            this.deliveryAddress = deliveryAddress;
        }
    }
} 