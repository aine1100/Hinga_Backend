package com.Hinga.farmMis.services;

import com.Hinga.farmMis.Dto.CartDto;
import com.Hinga.farmMis.Model.Cart;
import com.Hinga.farmMis.repository.CartRepository;
import com.Hinga.farmMis.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CartService {
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private ProductRepository productRepository;

    public CartService(CartRepository cartRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }

    public Cart addCart(CartDto cartDto){
        Cart cart=new Cart();
        cart.setOwner_email(cartDto.getOwner_email());
        cart.setProductName(cartDto.getProductName());
        cart.setQuantity(cartDto.getQuantity());
        cart.setPrice(cartDto.getPrice());
        cart.setDescription(cartDto.getDescription());
        cart.setProductCategory(cartDto.getCategory());
        cart.setProductUnits(cartDto.getUnits());
       return cartRepository.save(cart);
    }

    public List<Cart> getCarts(){
        return cartRepository.findAll();
    }

    public Cart getCartById(int id){
        if(!cartRepository.existsById(id)){
            return null;
        }
        return cartRepository.findById(id).get();
    }

    public Cart updateCart(CartDto cartDto,int id){
        if(!cartRepository.existsById(id)){
            return null;
        }
        Cart cart=cartRepository.findById(id).get();
        cart.setId(id);
        cart.setProductName(cartDto.getProductName());
        cart.setQuantity(cartDto.getQuantity());
        cart.setPrice(cartDto.getPrice());
        cart.setDescription(cartDto.getDescription());
        cart.setProductCategory(cartDto.getCategory());
        cart.setProductUnits(cartDto.getUnits());

        return cartRepository.save(cart);
    }

    public void deleteCart(int id){
        if(!cartRepository.existsById(id)){
            return;
        }
         cartRepository.deleteById(id);
    }



}
