package com.Hinga.farmMis.Model;


import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table
public class Crops {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String cropName;
    private  String cropType;
    private LocalDate farmDate;
    private LocalDate harvestDate;
    @ManyToOne
    @JoinColumn(name = "farm_id")
    private Farm farm;

    public Farm getFarm() {
        return farm;
    }

    public void setFarm(Farm farm) {
        this.farm = farm;
    }

    public LocalDate getHarvestDate() {
        return harvestDate;
    }

    public void setHarvestDate(LocalDate harvestDate) {
        this.harvestDate = harvestDate;
    }

    public LocalDate getFarmDate() {
        return farmDate;
    }

    public void setFarmDate(LocalDate farmDate) {
        this.farmDate = farmDate;
    }

    public String getCropType() {
        return cropType;
    }

    public void setCropType(String cropType) {
        this.cropType = cropType;
    }

    public String getCropName() {
        return cropName;
    }

    public void setCropName(String cropName) {
        this.cropName = cropName;
    }
}
