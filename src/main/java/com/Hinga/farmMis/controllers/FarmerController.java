package com.Hinga.farmMis.controllers;

import com.Hinga.farmMis.Model.Farmer;
import com.Hinga.farmMis.services.FarmerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api")
public class FarmerController {
  private final FarmerService farmerService;


    @Autowired
    public FarmerController(FarmerService farmerService) {
        this.farmerService = farmerService;
    }

    @GetMapping("/hello")
    public String Greet(){
        return "Welcome to Farmer Controller";
    }

    @PostMapping("/addfarmer")
    public Farmer postFarmer(@RequestBody Farmer farmer){
       return  farmerService.addFarmer(farmer);
    }
    @GetMapping("/farmer")
    public List<Farmer> getFarmer(){
        return farmerService.getFarmers();
    }
}
