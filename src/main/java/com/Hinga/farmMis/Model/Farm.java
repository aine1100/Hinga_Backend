package com.Hinga.farmMis.Model;



import jakarta.persistence.*;

import java.util.List;

@Entity
@Table
public class Farm {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private int farmId;
    private String Location;
    private double area;

    public Farm(String location, double area, Farmer farmer) {
        Location = location;
        this.area = area;
        this.farmer = farmer;
    }

    @ManyToOne
    @JoinColumn(name = "farmer_id")
    private Farmer farmer;
    @OneToMany(mappedBy = "farm")
    private List<Crops> crops;

    public Farm(String location, double area, Farmer farmer, List<Crops> crops) {
        Location = location;
        this.area = area;
        this.farmer = farmer;
        this.crops = crops;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public double getArea() {
        return area;
    }

    public void setArea(double area) {
        this.area = area;
    }

    public Farmer getFarmer() {
        return farmer;
    }

    public void setFarmer(Farmer farmer) {
        this.farmer = farmer;
    }

    public List<Crops> getCrops() {
        return crops;
    }

    public void setCrops(List<Crops> crops) {
        this.crops = crops;
    }
}
