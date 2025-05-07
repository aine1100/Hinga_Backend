package com.Hinga.farmMis.repository;

import com.Hinga.farmMis.Model.Orders;
import com.Hinga.farmMis.Model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Orders, Long> {

    // Find orders by buyer (where any cart in the order belongs to the buyer)
    @Query("SELECT DISTINCT o FROM Orders o WHERE NOT EXISTS (" +
            "SELECT c FROM o.carts c WHERE c.buyer != :buyer)")
    List<Orders> findByBuyer(@Param("buyer") Users buyer);

    @Query("SELECT DISTINCT o FROM Orders o JOIN o.carts c WHERE c.livestock.farmer = :farmer")
    List<Orders> findByFarmer(@Param("farmer") Users farmer);


}