package com.Hinga.farmMis.repository;

import com.Hinga.farmMis.Model.Farm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FarmRepository extends JpaRepository<Farm,Number> {
}
