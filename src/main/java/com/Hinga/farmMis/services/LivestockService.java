package com.Hinga.farmMis.services;

import com.Hinga.farmMis.Model.Livestock;
import com.Hinga.farmMis.repository.FarmRepository;
import com.Hinga.farmMis.repository.LivestockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LivestockService {
    @Autowired
    private  LivestockRepository livestockRepository;
    @Autowired
    private  FarmRepository farmRepository;

    public LivestockService() {}
    public Livestock createLivestock(Livestock livestock,Long farmId) {
        if(farmRepository.findById(farmId).isEmpty()){
            throw new RuntimeException("farm not found");
        }
        livestock.setFarmId(farmId);
        return livestockRepository.save(livestock);
    }

    public Optional<List<Livestock>> getLivestock(Long farmId) {
        if(farmRepository.findById(farmId).isEmpty()){
            throw new RuntimeException("farm not found");
        }
        return Optional.ofNullable(livestockRepository.findByFarmId(farmId));

    }

    public Livestock getLiveStockById(Long Id,Long farmId) {
        if(farmRepository.findById(farmId).isEmpty()){
            throw new RuntimeException("farm not found");
        }
        if(livestockRepository.findById(Id).isEmpty()){
            throw new RuntimeException("livestock not found");
        }
        return livestockRepository.findById(Id).get();
    }

    public Livestock updateLiveStock(Long farmId,Long Id,Livestock livestock){
        if(farmRepository.findById(farmId).isEmpty()){
            throw new RuntimeException("farm not found");
        }
        if(livestockRepository.findById(Id).isEmpty()){
            throw new RuntimeException("livestock not found");
        }
        Livestock update=livestockRepository.findById(Id).get();
        if(!update.getFarmId().equals(farmId)){
            throw new RuntimeException("The Livestock with this farmId does not exist");
        }
        update.setId(Id);
        update.setType(livestock.getType());
        update.setCount(livestock.getCount());
        update.setStatus(livestock.getStatus());
        update.setDescription(livestock.getDescription());
        return livestockRepository.save(update);

    }

    public boolean deleteLiveStock(Long farmId,Long Id) {
        if(farmRepository.findById(farmId).isEmpty()){
            throw new RuntimeException("farm not found");

        }
        if(livestockRepository.findById(Id).isEmpty()){
            throw new RuntimeException("livestock not found");
        }
       if(livestockRepository.findById(Id).get().getFarmId().equals(farmId)){
           livestockRepository.delete(livestockRepository.findById(Id).get());
           return true;
       }
       return false;

    }

}
