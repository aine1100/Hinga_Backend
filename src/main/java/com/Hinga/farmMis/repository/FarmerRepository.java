package com.Hinga.farmMis.repository;

import com.Hinga.farmMis.Model.Farmer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FarmerRepository extends JpaRepository<Farmer,Integer> {
}
