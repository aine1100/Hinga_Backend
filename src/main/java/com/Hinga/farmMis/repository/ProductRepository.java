package com.Hinga.farmMis.repository;

import com.Hinga.farmMis.Model.Products;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Products, Integer> {
    // Additional query methods can be defined here if needed
}
