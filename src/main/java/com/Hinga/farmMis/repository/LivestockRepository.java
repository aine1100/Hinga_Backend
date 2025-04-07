package com.Hinga.farmMis.repository;

import com.Hinga.farmMis.Model.Livestock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LivestockRepository extends JpaRepository<Livestock,Long> {
    public List<Livestock> findByFarmId(Long farmId);

    Long id(Long id);
}
