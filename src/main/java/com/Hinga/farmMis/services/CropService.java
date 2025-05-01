//package com.Hinga.farmMis.services;
//
//import com.Hinga.farmMis.Model.Crops;
//import com.Hinga.farmMis.repository.CropRepository;
//import com.Hinga.farmMis.repository.FarmRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDate;
//import java.util.List;
//import java.util.Optional;
//
//@Service
//public class CropService {
//
//    @Autowired
//    private CropRepository cropRepository;
//
//    @Autowired
//    private FarmRepository farmRepository;
//
//    public Crops createCrop(Crops crops, Long farmId) {
//        if (farmRepository.findById(farmId).isEmpty()) {
//            throw new IllegalArgumentException("Farm not found with ID: " + farmId);
//        }
//        crops.setFarmId(farmId);
//        return cropRepository.save(crops);
//    }
//
//    public List<Crops> getCrops(Long farmId) {
//        if (farmRepository.findById(farmId).isEmpty()) {
//            throw new IllegalArgumentException("Farm not found with ID: " + farmId);
//        }
//        return cropRepository.findByFarmId(farmId);
//    }
//
//    public Optional<Crops> getCropById(Long farmId, Long cropId) {
//        Optional<Crops> crop = cropRepository.findById(cropId);
//        return crop.filter(f -> f.getFarmId().equals(farmId));
//    }
//
//    public Crops UpdateCrop(Crops crop,Long farmId,Long cropId){
//        if(farmRepository.findById(farmId).isEmpty()){
//            throw new IllegalArgumentException("Farm with this id is not found"+farmId);
//        }
//
//        Optional<Crops> myCrop= Optional.of(cropRepository.findById(cropId).get());
//        if(myCrop.get().getFarmId().equals(farmId)){
//            crop.setId(cropId);
//            crop.setCropName(crop.getCropName());
//            crop.setFarmId(farmId);
//            crop.setFarmDate(LocalDate.now());
//            crop.setHarvestDate(LocalDate.now());
//
//            return cropRepository.save(crop);
//        }
//        return null;
//
//    }
//
//    public boolean deleteCrop(Long farmId,Long cropId){
//        if(farmRepository.findById(farmId).isEmpty()){
//            throw new IllegalArgumentException("Farm with this id is not found"+farmId);
//        }
//       Optional<Crops> myCrop=cropRepository.findById(cropId);
//        if(myCrop.isPresent() && myCrop.get().getFarmId().equals(farmId)){
//            cropRepository.deleteById(cropId);
//            return true;
//        }
//        return false;
//    }
//}
