package com.Hinga.farmMis.controllers.user;

import com.Hinga.farmMis.Constants.UserRoles;
import com.Hinga.farmMis.Model.Cart;
import com.Hinga.farmMis.Model.Users;
import com.Hinga.farmMis.repository.UserRepository;
import com.Hinga.farmMis.services.CartService;
import com.Hinga.farmMis.services.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    private final CartService cartService;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Autowired
    public CartController(CartService cartService, JwtService jwtService, UserRepository userRepository) {
        this.cartService = cartService;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @PostMapping("/create-cart")
    public ResponseEntity<?> createCart(@RequestHeader("Authorization") String token, @RequestBody Cart cartDto) {
        try {
            Long userId = extractUserId(token);
            Users buyer = userRepository.findById(userId);
            if (buyer == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            // Set the buyer field to the authenticated user, overriding any client-provided buyer
            cartDto.setBuyer(buyer);



            Cart createdCart = cartService.createCart(cartDto);


            return new ResponseEntity<>(createdCart, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Unauthorized: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping
    public ResponseEntity<?> getCart(@RequestHeader("Authorization") String token) {
        try {
            Long userId = extractUserId(token);
            List<Cart> carts = cartService.getCartByUserId(userId);
            return new ResponseEntity<>(carts, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Unauthorized: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCartById(@RequestHeader("Authorization") String token, @PathVariable("id") Long id) {
        try {
            Long userId = extractUserId(token);
            Cart cart = cartService.getCartById(userId,id);
            return new ResponseEntity<>(cart, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Server error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{cartId}")
    public ResponseEntity<?> updateCart(@RequestHeader("Authorization") String token,
                                        @PathVariable("cartId") Long cartId,
                                        @RequestBody Cart cartDto) {
        try {
            Long userId = extractUserId(token);
            // Ensure the cart belongs to the user
            Cart cart = cartService.getCartById(userId, cartId);
            Cart updatedCart = cartService.updateCartItem(cartId, cartDto.getQuantity());
            return new ResponseEntity<>(updatedCart, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Unauthorized: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCart(@RequestHeader("Authorization") String token,
                                        @PathVariable("id") Long id) {
        try {
            Long userId = extractUserId(token);
            boolean deleted = cartService.deleteCartItem(id,userId);
            if (deleted) {
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Cart not found or access denied", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Unauthorized: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    private Long extractUserId(String token) throws Exception {
        if (token == null || !token.startsWith("Bearer ")) {
            throw new Exception("Invalid token format");
        }
        String jwtToken = token.substring(7);
        if (!jwtService.isTokenValid(jwtToken)) {
            throw new Exception("Invalid or expired token");
        }
        return jwtService.extractId(jwtToken);
    }
}
