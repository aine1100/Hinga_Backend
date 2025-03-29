package com.Hinga.farmMis.services;

import com.Hinga.farmMis.Model.Farmer;
import com.Hinga.farmMis.repository.FarmerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class FarmerService {

    public FarmerService() {}

    @Autowired
    private FarmerRepository farmerRepository;
    public Farmer addFarmer(Farmer farmer) {
        farmerRepository.save(farmer);
        return farmer;
    }

    public List<Farmer> getFarmers() {
        return farmerRepository.findAll();
    }


    public Farmer getFarmer(int id) {
        return farmerRepository.findById(1).get();

    }

    public void deleteFarmer(int id) {
        Farmer farmer = farmerRepository.findById(id).get();
        if(farmer != null) {
            farmerRepository.delete(farmer);
        }
    }

}
