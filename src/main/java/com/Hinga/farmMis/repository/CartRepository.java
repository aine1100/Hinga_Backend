package com.Hinga.farmMis.repository;

import com.Hinga.farmMis.Model.Cart;
import com.Hinga.farmMis.Model.Livestock;
import com.Hinga.farmMis.Model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Cart findByLivestockAndBuyer(Livestock livestock, Users buyer);
    List<Cart> findByBuyer(Users buyer);
    void deleteByBuyer(Users buyer);
}
