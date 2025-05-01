package com.Hinga.farmMis.Model;

import com.Hinga.farmMis.utils.Payments;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
//
//@Entity
//@NoArgsConstructor
//@AllArgsConstructor
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Oid;
    @ManyToOne
    private Products products;
    private int quantity;
    private double price;
    @Embedded
    private Payments payments;
    private LocalDate orderDate;


}
