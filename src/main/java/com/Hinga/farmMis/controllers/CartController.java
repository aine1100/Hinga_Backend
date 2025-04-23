package com.Hinga.farmMis.controllers;

import com.Hinga.farmMis.Dto.CartDto;
import com.Hinga.farmMis.Model.Cart;
import com.Hinga.farmMis.services.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    private final CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/create-cart")
    public ResponseEntity<?> createCart(@RequestBody CartDto cartDto) {
        Cart createdCart = cartService.addCart(cartDto);
        return new ResponseEntity<>(createdCart, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<?> getCart() {
        List<Cart> carts = cartService.getCarts();
        return new ResponseEntity<>(carts, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCartById(@PathVariable("id") int id) {
        Cart cart = cartService.getCartById(id);
        if (cart == null) {
            return new ResponseEntity<>("Cart not found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(cart, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCart(@PathVariable("id") int id, @RequestBody CartDto cartDto) {
        Cart updatedCart = cartService.updateCart(cartDto, id);
        if (updatedCart == null) {
            return new ResponseEntity<>("Cart not found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(updatedCart, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCart(@PathVariable("id") int id) {
        try {
            cartService.deleteCart(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Cart not found", HttpStatus.NOT_FOUND);
        }
    }
}