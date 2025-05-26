package com.Hinga.farmMis.services;

import com.Hinga.farmMis.Constants.OrderStatus;
import com.Hinga.farmMis.Constants.PaymentStatus;
import com.Hinga.farmMis.Dto.response.FarmerOrderSummary;
import com.Hinga.farmMis.Dto.response.OrderResponse;
import com.Hinga.farmMis.Model.Orders;
import com.Hinga.farmMis.Model.Users;
import com.Hinga.farmMis.Model.Cart;
import com.Hinga.farmMis.repository.LivestockRepository;
import com.Hinga.farmMis.repository.OrderRepository;
import com.Hinga.farmMis.repository.CartRepository;
import com.Hinga.farmMis.repository.UserRepository;
import com.Hinga.farmMis.Dto.Request.MultiCartOrderRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;

@Service
public class OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final LivestockRepository livestockRepository;
    private final CartService cartService;
    private final NotificationService notificationService;

    public OrderService(OrderRepository orderRepository, CartRepository cartRepository, 
                       UserRepository userRepository, LivestockRepository livestockRepository,
                       CartService cartService, NotificationService notificationService) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.livestockRepository = livestockRepository;
        this.cartService = cartService;
        this.notificationService = notificationService;
    }

    // Create an order from a single cart
    @Transactional
    public OrderResponse createOrderFromCart(Orders orders) {
        // Validate the input Orders object
        if (orders == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }
        // Use the first cart in the carts list for backward compatibility
        if (orders.getCarts() == null || orders.getCarts().isEmpty() || orders.getCarts().get(0) == null || orders.getCarts().get(0).getId() == null) {
            throw new IllegalArgumentException("Cart ID is required");
        }
        if (orders.getDeliveryAddress() == null ||
                orders.getDeliveryAddress().getProvince() == null || orders.getDeliveryAddress().getProvince().trim().isEmpty() ||
                orders.getDeliveryAddress().getDistrict() == null || orders.getDeliveryAddress().getDistrict().trim().isEmpty() ||
                orders.getDeliveryAddress().getSector() == null || orders.getDeliveryAddress().getSector().trim().isEmpty() ||
                orders.getDeliveryAddress().getCell() == null || orders.getDeliveryAddress().getCell().trim().isEmpty() ||
                orders.getDeliveryAddress().getVillage() == null || orders.getDeliveryAddress().getVillage().trim().isEmpty()) {
            throw new IllegalArgumentException("All delivery address fields are required");
        }

        // Fetch the cart with livestock and farmer
        Cart cart = cartRepository.findById(orders.getCarts().get(0).getId())
                .orElseThrow(() -> new IllegalArgumentException("Cart not found with ID: " + orders.getCarts().get(0).getId()));

        // Validate relationships
        if (cart.getLivestock() == null) {
            throw new IllegalArgumentException("Cart ID " + cart.getId() + " has no associated livestock");
        }
        if (cart.getLivestock().getFarmer() == null) {
            throw new IllegalArgumentException("Livestock ID " + cart.getLivestock().getLivestockId() + " has no associated farmer");
        }

        // Validate the buyer
        Users buyer = userRepository.findById(cart.getBuyer().getId());
        if(buyer == null) {
            throw new IllegalArgumentException("Buyer not found with ID: " + cart.getBuyer().getId());
        }

        if (!buyer.getId().equals(cart.getBuyer().getId())) {
            throw new IllegalArgumentException("Unauthorized: Cart does not belong to this user");
        }

        // Validate stock
        if (cart.getQuantity() > cart.getLivestock().getQuantity()) {
            throw new IllegalArgumentException("Requested quantity (" + cart.getQuantity() + ") exceeds available stock (" + cart.getLivestock().getQuantity() + ")");
        }

        // Create the order
        Orders order = new Orders();
        List<Cart> carts = new ArrayList<>();
        carts.add(cart);
        order.setCarts(carts);
        order.setOrderDate(LocalDate.now());
        order.setDeliveryDate(orders.getDeliveryDate() != null ? orders.getDeliveryDate() : LocalDate.now().plusDays(7));
        order.setDeliveryAddress(orders.getDeliveryAddress());
        order.setOrderStatus(OrderStatus.PENDING);
        
        // Debug logging for incoming order
        System.out.println("=== Service Layer Debug ===");
        System.out.println("Incoming Order Buyer Name: " + orders.getBuyerName());
        System.out.println("Incoming Order Buyer Phone: " + orders.getBuyerPhone());
        
        // Set buyer information from the incoming order
        if (orders.getBuyerName() != null && !orders.getBuyerName().trim().isEmpty()) {
            order.setBuyerName(orders.getBuyerName());
        } else {
            System.out.println("Warning: Buyer name is null or empty in incoming order");
        }
        
        if (orders.getBuyerPhone() != null && !orders.getBuyerPhone().trim().isEmpty()) {
            order.setBuyerPhone(orders.getBuyerPhone());
        } else {
            System.out.println("Warning: Buyer phone is null or empty in incoming order");
        }
        
        System.out.println("Order Object Before Save - Buyer Name: " + order.getBuyerName());
        System.out.println("Order Object Before Save - Buyer Phone: " + order.getBuyerPhone());

        // Save the order first
        Orders savedOrder = orderRepository.save(order);
        
        // Debug logging after save
        System.out.println("Saved Order - ID: " + savedOrder.getId());
        System.out.println("Saved Order - Buyer Name: " + savedOrder.getBuyerName());
        System.out.println("Saved Order - Buyer Phone: " + savedOrder.getBuyerPhone());
        System.out.println("=========================");
        
        logger.info("Saved order with ID: {}, associated cart: {}", savedOrder.getId(), cart.getId());

        // Reduce livestock quantity
        int newStock = (int) (cart.getLivestock().getQuantity() - cart.getQuantity());
        if (newStock < 0) {
            throw new IllegalArgumentException("Stock cannot be negative after order");
        }
        cart.getLivestock().setQuantity(newStock);

        // Save the updated livestock
        livestockRepository.save(cart.getLivestock());

        // Update cart with order reference and mark as ordered
        cart.getOrders().add(savedOrder);
        cart.setOrdered(true);
        cartRepository.save(cart);

        // Send notification to farmer
        Users farmer = cart.getLivestock().getFarmer();
        notificationService.createNotification(
            farmer,
            "New order #" + savedOrder.getId() + " has been placed for your livestock.",
            "ORDER_CREATED",
            savedOrder
        );

        // Create response with buyer information
        OrderResponse response = new OrderResponse();
        response.setId(savedOrder.getId());
        response.setOrderDate(savedOrder.getOrderDate());
        response.setDeliveryDate(savedOrder.getDeliveryDate());
        response.setOrderStatus(savedOrder.getOrderStatus());
        response.setPaymentStatus(savedOrder.getPaymentStatus());
        response.setDeliveryAddress(savedOrder.getDeliveryAddress());
        response.setCarts(savedOrder.getCarts());
        
        // Add buyer information from the saved order
        response.setBuyerName(savedOrder.getBuyerName());
        response.setBuyerPhone(savedOrder.getBuyerPhone());
        response.setBuyerEmail(buyer.getEmail()); // Keep email from user object as it's not in the request

        return response;
    }

    // Get all orders for a buyer
    public List<Orders> getOrdersForBuyer(Long buyerId) {
        Users buyer = userRepository.findById(buyerId);
        if(buyer == null) {
               throw new IllegalArgumentException("Buyer not found with ID: " + buyerId);
        }
        if (buyer.getUserRole() != com.Hinga.farmMis.Constants.UserRoles.BUYER) {
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
        if (farmer.getUserRole() != com.Hinga.farmMis.Constants.UserRoles.FARMER) {
            throw new IllegalArgumentException("User is not a farmer");
        }
        
        List<Orders> orders = orderRepository.findByFarmer(farmer);
        logger.info("Retrieved {} orders for farmer ID: {}", orders.size(), farmerId);
        
        // Add detailed logging for debugging
        for (Orders order : orders) {
            logger.debug("Order ID: {}, Status: {}, Carts: {}", 
                order.getId(), 
                order.getOrderStatus(),
                order.getCarts().stream()
                    .map(cart -> String.format("Cart[ID=%d, LivestockID=%d]", 
                        cart.getId(), 
                        cart.getLivestock() != null ? cart.getLivestock().getLivestockId() : -1))
                    .collect(Collectors.joining(", "))
            );
        }
        
        return orders;
    }

    // Approve an order (set status to APPROVED)
    @Transactional
    public Orders approveOrder(Long orderId, Long farmerId) {
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + orderId));

        // Verify the farmer owns the livestock in the order
        boolean isFarmerAuthorized = order.getCarts().stream()
                .anyMatch(cart -> cart.getLivestock().getFarmer().getId().equals(farmerId));
        if (!isFarmerAuthorized) {
            throw new IllegalArgumentException("Unauthorized: Farmer does not own the livestock in this order");
        }

        order.setOrderStatus(OrderStatus.APPROVED);
        Orders savedOrder = orderRepository.save(order);

        // Send notification to buyer
        Users buyer = order.getCarts().get(0).getBuyer();
        notificationService.createNotification(
            buyer,
            "Your order #" + orderId + " has been approved by the farmer.",
            "ORDER_APPROVED",
            savedOrder
        );

        return savedOrder;
    }

    // Cancel an order (set status to CANCELLED)
    @Transactional
    public Orders cancelOrder(Long orderId, Long farmerId) {
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + orderId));

        // Verify the farmer owns the livestock in the order
        boolean isFarmerAuthorized = order.getCarts().stream()
                .anyMatch(cart -> cart.getLivestock().getFarmer().getId().equals(farmerId));
        if (!isFarmerAuthorized) {
            throw new IllegalArgumentException("Unauthorized: Farmer does not own the livestock in this order");
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
        Orders savedOrder = orderRepository.save(order);

        // Send notification to buyer
        Users buyer = order.getCarts().get(0).getBuyer();
        notificationService.createNotification(
            buyer,
            "Your order #" + orderId + " has been cancelled by the farmer.",
            "ORDER_CANCELLED",
            savedOrder
        );

        return savedOrder;
    }

    // Get order details by ID
    public Orders getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + orderId));
    }

    // Create an order from multiple carts
    @Transactional
    public OrderResponse createOrderFromCarts(Long buyerId, MultiCartOrderRequest request) {
        if (request == null || request.getCartIds() == null || request.getCartIds().isEmpty()) {
            throw new IllegalArgumentException("Cart IDs are required");
        }
        if (request.getDeliveryAddress() == null ||
                request.getDeliveryAddress().getProvince() == null || request.getDeliveryAddress().getProvince().trim().isEmpty() ||
                request.getDeliveryAddress().getDistrict() == null || request.getDeliveryAddress().getDistrict().trim().isEmpty() ||
                request.getDeliveryAddress().getSector() == null || request.getDeliveryAddress().getSector().trim().isEmpty() ||
                request.getDeliveryAddress().getCell() == null || request.getDeliveryAddress().getCell().trim().isEmpty() ||
                request.getDeliveryAddress().getVillage() == null || request.getDeliveryAddress().getVillage().trim().isEmpty()) {
            throw new IllegalArgumentException("All delivery address fields are required");
        }

        Users buyer = userRepository.findById(buyerId);
        if(buyer == null) {
            throw new IllegalArgumentException("Buyer not found with ID: " + buyerId);
        }

        // Fetch carts with livestock and farmer eagerly
        List<Cart> carts = cartRepository.findByIdInAndBuyerId(request.getCartIds(), buyerId);
        if (carts.isEmpty()) {
            throw new IllegalArgumentException("No valid carts found for the provided IDs");
        }

        // Validate carts
        for (Cart cart : carts) {
            logger.debug("Processing cart ID: {}, livestock ID: {}, buyer ID: {}",
                    cart.getId(),
                    cart.getLivestock() != null ? cart.getLivestock().getLivestockId() : "null",
                    cart.getBuyer() != null ? cart.getBuyer().getId() : "null");
            if (cart.getLivestock() == null) {
                throw new IllegalArgumentException("Cart ID " + cart.getId() + " has no associated livestock");
            }
            if (cart.getLivestock().getFarmer() == null) {
                throw new IllegalArgumentException("Livestock ID " + cart.getLivestock().getLivestockId() + " has no associated farmer");
            }
            if (!cart.getBuyer().getId().equals(buyerId)) {
                throw new IllegalArgumentException("Unauthorized: Cart ID " + cart.getId() + " does not belong to this user");
            }
            if (cart.getQuantity() > cart.getLivestock().getQuantity()) {
                throw new IllegalArgumentException("Requested quantity (" + cart.getQuantity() + ") exceeds available stock (" + cart.getLivestock().getQuantity() + ") for cart ID " + cart.getId());
            }
        }

        // Create the order
        Orders order = new Orders();
        order.setCarts(carts);
        order.setOrderDate(LocalDate.now());
        order.setDeliveryDate(LocalDate.now().plusDays(7));
        order.setDeliveryAddress(request.getDeliveryAddress());
        order.setOrderStatus(OrderStatus.PENDING);

        // Save the order first
        Orders savedOrder = orderRepository.save(order);
        logger.info("Saved order with ID: {}, associated carts: {}", savedOrder.getId(), 
            carts.stream().map(Cart::getId).collect(Collectors.toList()));

        // Update livestock quantity and mark carts as ordered
        for (Cart cart : carts) {
            int newStock = (int) (cart.getLivestock().getQuantity() - cart.getQuantity());
            if (newStock < 0) {
                throw new IllegalArgumentException("Stock cannot be negative for livestock ID " + cart.getLivestock().getLivestockId());
            }
            cart.getLivestock().setQuantity(newStock);
            livestockRepository.save(cart.getLivestock());

            // Update cart with order reference and mark as ordered
            cart.getOrders().add(savedOrder);
            cart.setOrdered(true);
            cartRepository.save(cart);
        }

        // Send notification to farmer
        for (Cart cart : savedOrder.getCarts()) {
            Users farmer = cart.getLivestock().getFarmer();
            notificationService.createNotification(
                farmer,
                "New order #" + savedOrder.getId() + " has been placed for your livestock.",
                "ORDER_CREATED",
                savedOrder
            );
        }

        // Create response with buyer information
        OrderResponse response = new OrderResponse();
        response.setId(savedOrder.getId());
        response.setOrderDate(savedOrder.getOrderDate());
        response.setDeliveryDate(savedOrder.getDeliveryDate());
        response.setOrderStatus(savedOrder.getOrderStatus());
        response.setPaymentStatus(savedOrder.getPaymentStatus());
        response.setDeliveryAddress(savedOrder.getDeliveryAddress());
        response.setCarts(savedOrder.getCarts());
        
        // Add buyer information
        response.setBuyerName(buyer.getFirstName() + " " + buyer.getLastName());
        response.setBuyerPhone(buyer.getPhoneNumber());
        response.setBuyerEmail(buyer.getEmail());

        return response;
    }

    // Get order summary for a farmer
    public FarmerOrderSummary getOrderSummaryForFarmer(Long farmerId) {
        Users farmer = userRepository.findById(farmerId);
        if(farmer == null) {
            throw new IllegalArgumentException("Farmer not found with ID: " + farmerId);
        }
        if (farmer.getUserRole() != com.Hinga.farmMis.Constants.UserRoles.FARMER) {
            throw new IllegalArgumentException("User is not a farmer");
        }
        
        List<Orders> orders = orderRepository.findByFarmer(farmer);
        FarmerOrderSummary summary = new FarmerOrderSummary();
        
        // Initialize counters
        Map<OrderStatus, Long> ordersByStatus = new HashMap<>();
        Map<PaymentStatus, Long> ordersByPaymentStatus = new HashMap<>();
        Map<String, Long> livestockTypeCounts = new HashMap<>();
        long totalLivestockOrdered = 0;
        List<FarmerOrderSummary.OrderWithBuyerInfo> ordersWithBuyerInfo = new ArrayList<>();
        
        // Process each order
        for (Orders order : orders) {
            // Count by status
            ordersByStatus.merge(order.getOrderStatus(), 1L, Long::sum);
            ordersByPaymentStatus.merge(order.getPaymentStatus(), 1L, Long::sum);
            
            // Create buyer info for this order
            FarmerOrderSummary.OrderWithBuyerInfo buyerInfo = new FarmerOrderSummary.OrderWithBuyerInfo();
            buyerInfo.setOrderId(order.getId());
            buyerInfo.setOrderStatus(order.getOrderStatus());
            
            // Get buyer information from the first cart (since all carts in an order belong to the same buyer)
            if (!order.getCarts().isEmpty()) {
                Users buyer = order.getCarts().get(0).getBuyer();
                buyerInfo.setBuyerName(buyer.getFirstName() + " " + buyer.getLastName());
                buyerInfo.setBuyerPhone(buyer.getPhoneNumber());
                buyerInfo.setBuyerEmail(buyer.getEmail());
                
                // Format delivery address
                if (order.getDeliveryAddress() != null) {
                    String address = String.format("%s, %s, %s, %s, %s",
                        order.getDeliveryAddress().getProvince(),
                        order.getDeliveryAddress().getDistrict(),
                        order.getDeliveryAddress().getSector(),
                        order.getDeliveryAddress().getCell(),
                        order.getDeliveryAddress().getVillage());
                    buyerInfo.setDeliveryAddress(address);
                }
            }
            
            ordersWithBuyerInfo.add(buyerInfo);
            
            // Process each cart in the order
            for (Cart cart : order.getCarts()) {
                if (cart.getLivestock() != null) {
                    // Add to total livestock count
                    totalLivestockOrdered += cart.getQuantity();
                    
                    // Count by livestock type
                    String livestockType = cart.getLivestock().getType();
                    livestockTypeCounts.merge(livestockType, cart.getQuantity(), Long::sum);
                }
            }
        }
        
        // Set summary values
        summary.setTotalOrders((long) orders.size());
        summary.setTotalLivestockOrdered(totalLivestockOrdered);
        summary.setOrdersByStatus(ordersByStatus);
        summary.setOrdersByPaymentStatus(ordersByPaymentStatus);
        summary.setLivestockTypeCounts(livestockTypeCounts);
        summary.setOrdersWithBuyerInfo(ordersWithBuyerInfo);
        
        return summary;
    }

    @Transactional
    public OrderResponse createOrderFromSelectedCarts(Orders order, List<Long> selectedCartIds, Long buyerId) {
        // Validate input
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }
        if (selectedCartIds == null || selectedCartIds.isEmpty()) {
            throw new IllegalArgumentException("At least one cart ID must be selected");
        }
        if (order.getDeliveryAddress() == null ||
                order.getDeliveryAddress().getProvince() == null || order.getDeliveryAddress().getProvince().trim().isEmpty() ||
                order.getDeliveryAddress().getDistrict() == null || order.getDeliveryAddress().getDistrict().trim().isEmpty() ||
                order.getDeliveryAddress().getSector() == null || order.getDeliveryAddress().getSector().trim().isEmpty() ||
                order.getDeliveryAddress().getCell() == null || order.getDeliveryAddress().getCell().trim().isEmpty() ||
                order.getDeliveryAddress().getVillage() == null || order.getDeliveryAddress().getVillage().trim().isEmpty()) {
            throw new IllegalArgumentException("All delivery address fields are required");
        }

        // Fetch carts with livestock and farmer eagerly
        List<Cart> selectedCarts = cartRepository.findByIdInAndBuyerId(selectedCartIds, buyerId);
        if (selectedCarts.isEmpty()) {
            throw new IllegalArgumentException("No valid carts found for the provided IDs");
        }

        // Validate carts
        for (Cart cart : selectedCarts) {
            if (cart.getLivestock() == null) {
                throw new IllegalArgumentException("Cart ID " + cart.getId() + " has no associated livestock");
            }
            if (cart.getLivestock().getFarmer() == null) {
                throw new IllegalArgumentException("Livestock ID " + cart.getLivestock().getLivestockId() + " has no associated farmer");
            }
            if (!cart.getBuyer().getId().equals(buyerId)) {
                throw new IllegalArgumentException("Unauthorized: Cart ID " + cart.getId() + " does not belong to this user");
            }
            if (cart.getQuantity() > cart.getLivestock().getQuantity()) {
                throw new IllegalArgumentException("Requested quantity (" + cart.getQuantity() + ") exceeds available stock (" + cart.getLivestock().getQuantity() + ") for cart ID " + cart.getId());
            }
        }

        // Set the selected carts to the order
        order.setCarts(selectedCarts);
        order.setOrderDate(LocalDate.now());
        order.setOrderStatus(OrderStatus.PENDING);

        // Save the order first
        Orders savedOrder = orderRepository.save(order);
        logger.info("Saved order with ID: {}, associated carts: {}", savedOrder.getId(), 
            selectedCarts.stream().map(Cart::getId).collect(Collectors.toList()));

        // Update livestock quantity and mark carts as ordered
        for (Cart cart : selectedCarts) {
            int newStock = (int) (cart.getLivestock().getQuantity() - cart.getQuantity());
            if (newStock < 0) {
                throw new IllegalArgumentException("Stock cannot be negative for livestock ID " + cart.getLivestock().getLivestockId());
            }
            cart.getLivestock().setQuantity(newStock);
            livestockRepository.save(cart.getLivestock());

            // Update cart with order reference and mark as ordered
            cart.getOrders().add(savedOrder);
            cart.setOrdered(true);
            cartRepository.save(cart);
        }

        // Send notification to farmer
        for (Cart cart : savedOrder.getCarts()) {
            Users farmer = cart.getLivestock().getFarmer();
            notificationService.createNotification(
                farmer,
                "New order #" + savedOrder.getId() + " has been placed for your livestock.",
                "ORDER_CREATED",
                savedOrder
            );
        }

        // Create response with buyer information
        OrderResponse response = new OrderResponse();
        response.setId(savedOrder.getId());
        response.setOrderDate(savedOrder.getOrderDate());
        response.setDeliveryDate(savedOrder.getDeliveryDate());
        response.setOrderStatus(savedOrder.getOrderStatus());
        response.setPaymentStatus(savedOrder.getPaymentStatus());
        response.setDeliveryAddress(savedOrder.getDeliveryAddress());
        response.setCarts(savedOrder.getCarts());
        
        // Add buyer information
        response.setBuyerName(order.getBuyerName());
        response.setBuyerPhone(order.getBuyerPhone());
        
        // Get buyer email from the first cart
        if (!selectedCarts.isEmpty() && selectedCarts.get(0).getBuyer() != null) {
            response.setBuyerEmail(selectedCarts.get(0).getBuyer().getEmail());
        }

        return response;
    }
}