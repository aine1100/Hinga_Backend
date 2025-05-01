//package com.Hinga.farmMis.services;
//
//import com.Hinga.farmMis.Model.Farm;
//import com.Hinga.farmMis.repository.FarmRepository;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.Optional;
//
//@Service
//public class FarmService {
//
//    private final FarmRepository farmRepository;
//
//    public FarmService(FarmRepository farmRepository) {
//        this.farmRepository = farmRepository;
//    }
//
//    // ✅ Create a farm with owner set
//    public Farm createFarm(Farm farm, String ownerEmail) {
//        farm.setOwnerEmail(ownerEmail); // Set the logged-in user's email as the owner
//        return farmRepository.save(farm);
//    }
//
//    // ✅ Retrieve farms that belong to a specific owner
//    public List<Farm> getFarmsByOwner(String ownerEmail) {
//        return farmRepository.findByOwnerEmail(ownerEmail);
//    }
//
//    // ✅ Get a farm by ID (only if it belongs to the user)
//    public Optional<Farm> getFarmById(Long id, String ownerEmail) {
//        Optional<Farm> farm = farmRepository.findById(id);
//        return farm.filter(f -> f.getOwnerEmail().equals(ownerEmail));
//    }
//
//    // ✅ Update a farm (only if the logged-in user owns it)
//    public Farm updateFarm(Long id, Farm updatedFarm, String ownerEmail) {
//        Optional<Farm> farm = farmRepository.findById(id);
//
//        if (farm.isPresent() && farm.get().getOwnerEmail().equals(ownerEmail)) {
//            updatedFarm.setId(id);
//            updatedFarm.setOwnerEmail(ownerEmail);
//            return farmRepository.save(updatedFarm);
//        }
//
//        return null; // Return null if the farm doesn't belong to the user
//    }
//
//    // ✅ Delete a farm (only if the logged-in user owns it)
//    public boolean deleteFarm(Long id, String ownerEmail) {
//        Optional<Farm> farm = farmRepository.findById(id);
//
//        if (farm.isPresent() && farm.get().getOwnerEmail().equals(ownerEmail)) {
//            farmRepository.deleteById(id);
//            return true;
//        }
//
//        return false; // Unauthorized delete attempt
//    }
//}
