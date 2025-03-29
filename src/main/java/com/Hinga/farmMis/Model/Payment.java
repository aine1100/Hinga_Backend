package com.Hinga.farmMis.Model;



import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private  int id;
    private LocalDate transcactionDate;
    private double amount;
    @ManyToOne
    @JoinColumn(name = "farmer_id")
    private Farmer farmer;

    public Farmer getFarmer() {
        return farmer;
    }

    public void setFarmer(Farmer farmer) {
        this.farmer = farmer;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDate getTranscactionDate() {
        return transcactionDate;
    }

    public void setTranscactionDate(LocalDate transcactionDate) {
        this.transcactionDate = transcactionDate;
    }


}
