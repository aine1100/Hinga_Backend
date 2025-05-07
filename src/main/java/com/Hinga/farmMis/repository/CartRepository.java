package com.Hinga.farmMis.repository;

import com.Hinga.farmMis.Model.Cart;
import com.Hinga.farmMis.Model.Livestock;
import com.Hinga.farmMis.Model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    // Find the most recent un-ordered cart for a specific livestock and buyer
    @Query("SELECT c FROM Cart c WHERE c.livestock = :livestock AND c.buyer = :buyer AND c.ordered = false ORDER BY c.id DESC")
    List<Cart> findUnorderedByLivestockAndBuyer(@Param("livestock") Livestock livestock, @Param("buyer") Users buyer);
    
    List<Cart> findByBuyer(Users buyer);
    List<Cart> findByBuyerAndOrderedFalse(Users buyer);
    void deleteByBuyer(Users buyer);
    
    @Query("SELECT c FROM Cart c JOIN FETCH c.livestock l JOIN FETCH l.farmer WHERE c.id IN :cartIds AND c.buyer.id = :buyerId")
    List<Cart> findByIdInAndBuyerId(@Param("cartIds") List<Long> cartIds, @Param("buyerId") Long buyerId);
}
