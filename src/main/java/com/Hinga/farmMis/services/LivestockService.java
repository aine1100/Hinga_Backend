//package com.Hinga.farmMis.services;
//
//import com.Hinga.farmMis.Model.Livestock;
//import com.Hinga.farmMis.Model.Users;
//import com.Hinga.farmMis.repository.FarmRepository;
//import com.Hinga.farmMis.repository.LivestockRepository;
//import com.Hinga.farmMis.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.Optional;
//
//@Service
//public class LivestockService {
//    @Autowired
//    private  LivestockRepository livestockRepository;
//    @Autowired
//    private  FarmRepository farmRepository;
//    @Autowired
//    private UserRepository userRepository;
//
//    public LivestockService() {}
//    public Livestock createLivestock(Livestock livestock,Long UserId) {
//        if(farmRepository.findById(farmId).isEmpty()){
//            throw new RuntimeException("farm not found");
//        }
//        Users farmer=userRepository.findById(farmId);
//        livestock.setFarmer(farmId);
//        return livestockRepository.save(livestock);
//    }
//
//    public Optional<List<Livestock>> getLivestock(Long farmerId) {
//        if(farmRepository.findById(farmerId).isEmpty()){
//            throw new RuntimeException("farm not found");
//        }
//        return Optional.ofNullable(livestockRepository.findByFarmerId(farmerId));
//
//    }
//
//    public Livestock getLiveStockById(Long Id,Long farmerId) {
//        if(farmRepository.findById(farmerId).isEmpty()){
//            throw new RuntimeException("farm not found");
//        }
//        if(livestockRepository.findById(Id).isEmpty()){
//            throw new RuntimeException("livestock not found");
//        }
//        return livestockRepository.findById(Id).get();
//    }
//
//    public Livestock updateLiveStock(Long farmId,Long Id,Livestock livestock){
//        if(farmRepository.findById(farmId).isEmpty()){
//            throw new RuntimeException("farm not found");
//        }
//        if(livestockRepository.findById(Id).isEmpty()){
//            throw new RuntimeException("livestock not found");
//        }
//        Livestock update=livestockRepository.findById(Id).get();
//        if(!update.getFarmer().equals(farmId)){
//            throw new RuntimeException("The Livestock with this farmer Id does not exist");
//        }
//        update.setFarmer(Id);
//        update.setType(livestock.getType());
//        update.setCount(livestock.getCount());
//        update.setStatus(livestock.getStatus());
//        update.setDescription(livestock.getDescription());
//        return livestockRepository.save(update);
//
//    }
//
//    public boolean deleteLiveStock(Long farmId,Long Id) {
//        if(farmRepository.findById(farmId).isEmpty()){
//            throw new RuntimeException("farm not found");
//
//        }
//        if(livestockRepository.findById(Id).isEmpty()){
//            throw new RuntimeException("livestock not found");
//        }
//       if(livestockRepository.findById(Id).get().getFarmer().equals(farmId)){
//           livestockRepository.delete(livestockRepository.findById(Id).get());
//           return true;
//       }
//       return false;
//
//    }
//
//}
