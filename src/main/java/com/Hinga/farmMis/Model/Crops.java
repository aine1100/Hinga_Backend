package com.Hinga.farmMis.Model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
//
//@Entity
//@Table(name = "crop")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class Crops {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cropName;
    private LocalDate farmDate;
    private LocalDate harvestDate;

    // Ensures correct mapping in DB
    private Long farmId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCropName() {
        return cropName;
    }

    public void setCropName(String cropName) {
        this.cropName = cropName;
    }

    public LocalDate getFarmDate() {
        return farmDate;
    }

    public void setFarmDate(LocalDate farmDate) {
        this.farmDate = farmDate;
    }

    public LocalDate getHarvestDate() {
        return harvestDate;
    }

    public void setHarvestDate(LocalDate harvestDate) {
        this.harvestDate = harvestDate;
    }

    public Long getFarmId() {
        return farmId;
    }

    public void setFarmId(Long farmId) {
        this.farmId = farmId;
    }
}
