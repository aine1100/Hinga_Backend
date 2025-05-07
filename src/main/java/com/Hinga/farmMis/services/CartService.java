package com.Hinga.farmMis.services;

import com.Hinga.farmMis.Model.Cart;
import com.Hinga.farmMis.Model.Livestock;
import com.Hinga.farmMis.Model.Users;
import com.Hinga.farmMis.repository.CartRepository;
import com.Hinga.farmMis.repository.LivestockRepository;
import com.Hinga.farmMis.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final LivestockRepository livestockRepository;
    private final UserRepository userRepository;

    public CartService(CartRepository cartRepository, LivestockRepository livestockRepository, UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.livestockRepository = livestockRepository;
        this.userRepository = userRepository;
    }

    // ✅ Create or update existing item in cart
    public Cart createCart(Cart cart) {
        // Validate input
        if (cart == null) {
            throw new IllegalArgumentException("Cart cannot be null");
        }
        if (cart.getBuyer() == null || cart.getBuyer().getId() == null) {
            throw new IllegalArgumentException("Buyer ID is required");
        }
        if (cart.getQuantity() == null || cart.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        // Set livestock manually if null (for testing only)
        Long livestockId = 1L; // Hardcoded for testing
        Livestock livestock;
        if (cart.getLivestock() == null || cart.getLivestock().getLivestockId() == null) {
            livestock = livestockRepository.findById(livestockId)
                    .orElseThrow(() -> new IllegalArgumentException("Livestock not found with ID: " + livestockId));
            cart.setLivestock(livestock);
        } else {
            livestock = livestockRepository.findById(cart.getLivestock().getLivestockId())
                    .orElseThrow(() -> new IllegalArgumentException("Livestock not found with ID: " + cart.getLivestock().getLivestockId()));
        }
        // Fetch Buyer by buyer_id
        Users buyer = userRepository.findById(cart.getBuyer().getId());
        if (buyer == null) {
              throw  new IllegalArgumentException("Buyer not found");
        }
        // Validate quantity and status
        if (cart.getQuantity() > livestock.getQuantity()) {
            throw new IllegalArgumentException("Requested quantity (" + cart.getQuantity() + ") exceeds available stock (" + livestock.getQuantity() + ")");
        }

        // Check for existing un-ordered cart entries
        List<Cart> existingCarts = cartRepository.findUnorderedByLivestockAndBuyer(livestock, buyer);

        if (!existingCarts.isEmpty()) {
            // Get the most recent un-ordered cart
            Cart existingCart = existingCarts.get(0);
            
            // Update existing cart
            Long newQuantity = existingCart.getQuantity() + cart.getQuantity();
            if (newQuantity > livestock.getQuantity()) {
                throw new IllegalArgumentException("Total quantity (" + newQuantity + ") exceeds available stock (" + livestock.getQuantity() + ")");
            }
            existingCart.setQuantity(newQuantity);
            existingCart.setTotalPrice(newQuantity * existingCart.getUnitPrice());
            return cartRepository.save(existingCart);
        } else {
            // Create new cart
            Cart newCart = new Cart();
            newCart.setLivestock(livestock);
            newCart.setBuyer(buyer);
            newCart.setQuantity(cart.getQuantity());
            newCart.setUnitPrice((long) livestock.getPrice());
            newCart.setOrdered(false); // Ensure it's not marked as ordered
            newCart.calculateTotalPrice();
            return cartRepository.save(newCart);
        }
    }

    // ✅ Get all cart items for a specific user
    public List<Cart> getCartByUserId(Long userId) {
        Users user = userRepository.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }
        return cartRepository.findByBuyerAndOrderedFalse(user);
    }

    // ✅ Update quantity for a specific cart item
    public Cart updateCartItem(Long cartId, Long newQuantity) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found"));
        cart.setQuantity(newQuantity);
        cart.setTotalPrice(newQuantity * cart.getUnitPrice());
        return cartRepository.save(cart);
    }
    // ✅ Get cart by ID
    public Cart getCartById(Long userId, Long cartId) {
        Users user = userRepository.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        if (cart.getBuyer() == null || cart.getBuyer().getId() == null || !userId.equals(cart.getBuyer().getId())) {
            throw new RuntimeException("Unauthorized access to cart");
        }
        return cart;
    }

    // ✅ Delete a specific item from the cart
    public boolean deleteCartItem(Long cartId,Long UserId) {
        Users user = userRepository.findById(UserId);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        if (!cartRepository.existsById(cartId)) {
            throw new IllegalArgumentException("Cart item not found");
        }
        cartRepository.deleteById(cartId);
        return true;
    }

    // ✅ Clear the entire cart for a user
    public void clearCartByUserId(Long userId) {
        Users user = userRepository.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        cartRepository.deleteByBuyer(user);
    }

    // Delete multiple cart items by their IDs
    @Transactional
    public void deleteCartItemsByIds(List<Long> cartIds) {
        if (cartIds == null || cartIds.isEmpty()) {
            return;
        }
        
        // First find all carts
        List<Cart> carts = cartRepository.findAllById(cartIds);
        
        // Mark them as ordered
        for (Cart cart : carts) {
            cart.setOrdered(true);
            cartRepository.save(cart);
        }
        
        // Clear any relationships before deletion
        for (Cart cart : carts) {
            cart.setBuyer(null);
            cart.setLivestock(null);
            cartRepository.save(cart);
        }
        
        // Now delete them
        cartRepository.deleteAllById(cartIds);
    }
}