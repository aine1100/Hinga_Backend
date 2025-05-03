package com.Hinga.farmMis.services;

import com.Hinga.farmMis.Constants.OrderStatus;
import com.Hinga.farmMis.Model.Orders;
import com.Hinga.farmMis.Model.Users;
import com.Hinga.farmMis.Model.Cart;
import com.Hinga.farmMis.repository.OrderRepository;
import com.Hinga.farmMis.repository.CartRepository;
import com.Hinga.farmMis.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;

    public OrderService(OrderRepository orderRepository, CartRepository cartRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
    }

    // Create an order from a cart
    public Orders createOrderFromCart(Orders orders) {
        // Validate the input Orders object
        if (orders == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }
        if (orders.getCart() == null || orders.getCart().getId() == 0) {
            throw new IllegalArgumentException("Cart ID is required");
        }
        if (orders.getDeliveryAddress() == null ||
                orders.getDeliveryAddress().getProvince() == null || orders.getDeliveryAddress().getProvince().trim().isEmpty() ||
                orders.getDeliveryAddress().getDistrict() == null || orders.getDeliveryAddress().getDistrict().trim().isEmpty() ||
                orders.getDeliveryAddress().getSector() == null || orders.getDeliveryAddress().getSector().trim().isEmpty() ||
                orders.getDeliveryAddress().getCell() == null || orders.getDeliveryAddress().getCell().trim().isEmpty()
        || orders.getDeliveryAddress().getVillage() == null || orders.getDeliveryAddress().getVillage().trim().isEmpty()
        ) {
            throw new IllegalArgumentException("All delivery address fields are required");
        }

        // Fetch the cart
        Cart cart = cartRepository.findById(orders.getCart().getId())
                .orElseThrow(() -> new IllegalArgumentException("Cart not found with ID: " + orders.getCart().getId()));

        // Validate the buyer
        Users buyer = userRepository.findById(cart.getBuyer().getId());
        if(buyer == null) {
            throw new IllegalArgumentException("Buyer not found with ID: " + cart.getBuyer().getId());
        }


        // Redundant check: cart.getBuyer().getId() is already fetched from cart
        if (!buyer.getId().equals(cart.getBuyer().getId())) {
            throw new IllegalArgumentException("Unauthorized: Cart does not belong to this user");
        }

        // Validate stock
        if (cart.getQuantity() > cart.getLivestock().getQuantity()) {
            throw new IllegalArgumentException("Requested quantity (" + cart.getQuantity() + ") exceeds available stock (" + cart.getLivestock().getQuantity() + ")");
        }

        // Create the order
        Orders order = new Orders();
        order.setCart(cart);
        order.setOrderDate(LocalDate.now());
        order.setDeliveryDate(LocalDate.now().plusDays(7)); // Example: 7 days from now
        order.setDeliveryAddress(orders.getDeliveryAddress()); // Use the provided delivery address
        order.setOrderStatus(OrderStatus.PENDING);

        // Save the order
        Orders savedOrder = orderRepository.save(order);

        // Add the order to the cart
        cart.getOrders().add(savedOrder);

        // Reduce livestock quantity
        Long newStock = cart.getLivestock().getQuantity() - cart.getQuantity();
        if (newStock < 0) {
            throw new IllegalArgumentException("Stock cannot be negative after order");
        }
        int newStock1=(int) Math.ceil(newStock);
        cart.getLivestock().setQuantity(newStock1);

        // Remove the cart item after the order is created
        cartRepository.delete(cart);

        return savedOrder;
    }

    // Get all orders for a buyer
    public List<Orders> getOrdersForBuyer(Long buyerId) {
        Users buyer = userRepository.findById(buyerId);
        if (buyer == null) {
            throw new IllegalArgumentException("Buyer not found with ID: " + buyerId);
        }


        if (!"BUYER".equals(buyer.getUserRole())) {
            throw new IllegalArgumentException("User is not a buyer");
        }
        return orderRepository.findByBuyer(buyer);
    }

    // Get all orders for a farmer (orders involving their livestock)
    public List<Orders> getOrdersForFarmer(Long farmerId) {
        Users farmer = userRepository.findById(farmerId);
        if(farmer == null) {
            throw new IllegalArgumentException("Farmer not found with ID: " + farmerId);
        }


        if (!"FARMER".equals(farmer.getUserRole())) {
            throw new IllegalArgumentException("User is not a farmer");
        }
        return orderRepository.findByFarmer(farmer);
    }
}