package com.Hinga.farmMis.repository;

import com.Hinga.farmMis.Model.Orders;
import com.Hinga.farmMis.Model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Orders, Long> {

    // Find orders by buyer (cart.buyer)
    @Query("SELECT o FROM Orders o WHERE o.cart.buyer = :buyer")
    List<Orders> findByBuyer(@Param("buyer") Users buyer);

    // Find orders by farmer (cart.livestock.farmer)
    @Query("SELECT o FROM Orders o WHERE o.cart.livestock.farmer = :farmer")
    List<Orders> findByFarmer(@Param("farmer") Users farmer);
}