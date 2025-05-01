package com.Hinga.farmMis.repository;

import com.Hinga.farmMis.Model.Livestock;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LivestockRepository extends JpaRepository<Livestock,Long> {
    public List<Livestock> findByFarmerId(Long id);
    public Livestock findByType(String name);
}
