package com.Hinga.farmMis.services.farmer;

import com.Hinga.farmMis.Model.Livestock;
import com.Hinga.farmMis.repository.LivestockRepository;
import com.Hinga.farmMis.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class LivestockService {

    private final LivestockRepository livestockRepository;
    private final UserRepository userRepository;
    @Value("${file.upload-dir}")
    private String uploadDir;

    public LivestockService(LivestockRepository livestockRepository, UserRepository userRepository) {
        this.livestockRepository = livestockRepository;
        this.userRepository = userRepository;
    }

    public Livestock addLivestock(Livestock livestock, MultipartFile imageFile) throws IOException {
        // Validate input
        if (imageFile == null || imageFile.isEmpty()) {
            throw new IllegalArgumentException("Image file is required");
        }

        // Handle image upload
        String imageUrl = saveImage(imageFile);
        livestock.setImageUrls(imageUrl);

        return livestockRepository.save(livestock);
    }

    private String saveImage(MultipartFile imageFile) throws IOException {
        // Ensure upload directory exists
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generate unique filename
        String originalFilename = imageFile.getOriginalFilename();
        String fileExtension = originalFilename != null ?
                originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
        String uniqueFilename = UUID.randomUUID() + fileExtension;

        // Save file
        Path filePath = uploadPath.resolve(uniqueFilename);
        Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Return relative path for web access
        return "/uploads/" + uniqueFilename;
    }

    public List<Livestock> getAllLiveStocks(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId is required");
        }
        if (userRepository.findById(userId) == null) {
            throw new IllegalArgumentException("User not found");
        }
        if (livestockRepository.findByFarmerId(userId).isEmpty()) {
            throw new IllegalArgumentException("User with this id does not have any livestock");
        }
        return livestockRepository.findByFarmerId(userId);
    }

    public List<Livestock> getAllLivestockForBuyers() {
        return livestockRepository.findAll();
    }

    public Optional<Livestock> getLiveStockById(Long id, Long userId) {
        if (userId == null || id == null) {
            throw new IllegalArgumentException("userId or id is required");
        }
        if (userRepository.findById(userId) == null) {
            throw new IllegalArgumentException("User not found");
        }
        if (livestockRepository.findByFarmerId(userId).isEmpty()) {
            throw new IllegalArgumentException("User with this id does not have any livestock");
        }
        return livestockRepository.findById(id);
    }

    public Optional<Livestock> getLiveStockByName(String name, Long userId) {
        if (userId == null || name == null) {
            throw new IllegalArgumentException("userId or name is required");
        }
        if (userRepository.findById(userId) == null) {
            throw new IllegalArgumentException("User not found");
        }
        if (livestockRepository.findByFarmerId(userId).isEmpty()) {
            throw new IllegalArgumentException("User with this id does not have any livestock");
        }
        return Optional.ofNullable(livestockRepository.findByType(name));
    }

    public Livestock updateLiveStock(Long userId, Long id, Livestock livestock) throws IOException {
        if (userRepository.findById(userId) == null) {
            throw new IllegalArgumentException("User not found");
        }
        if (livestockRepository.findByFarmerId(userId).isEmpty()) {
            throw new IllegalArgumentException("User with this id does not have any livestock");
        }
        if (livestockRepository.findById(id).isEmpty()) {
            throw new IllegalArgumentException("Livestock not found");
        }
        Livestock existingLivestock = livestockRepository.findById(id).get();

        // Update fields if provided
        if (livestock.getType() != null) existingLivestock.setType(livestock.getType());
        if (livestock.getBreed() != null) existingLivestock.setBreed(livestock.getBreed());
        if (livestock.getCount() != 0) existingLivestock.setCount(livestock.getCount());
        if (livestock.getDescription() != null) existingLivestock.setDescription(livestock.getDescription());
        if (livestock.getWeight() != 0.0) existingLivestock.setWeight(livestock.getWeight());
        if (livestock.getPrice() != 0.0) existingLivestock.setPrice(livestock.getPrice());
        if (livestock.getQuantity() != 0) existingLivestock.setQuantity(livestock.getQuantity());
        if (livestock.getStatus() != null) existingLivestock.setStatus(livestock.getStatus());
        if (livestock.getBirthDate() != null) existingLivestock.setBirthDate(livestock.getBirthDate());

        return livestockRepository.save(existingLivestock);
    }

    public Livestock updateLiveStock(Long userId, Long id, Livestock livestock, MultipartFile imageFile) throws IOException {
        Livestock updatedLivestock = updateLiveStock(userId, id, livestock);
        if (imageFile != null && !imageFile.isEmpty()) {
            // Delete old image
            deleteImage(updatedLivestock.getImageUrls());
            // Save new image
            String newImageUrl = saveImage(imageFile);
            updatedLivestock.setImageUrls(newImageUrl);
            return livestockRepository.save(updatedLivestock);
        }
        return updatedLivestock;
    }

    public void deleteLivestock(Long id, Long userId) throws IOException {
        if (userRepository.findById(userId) == null) {
            throw new IllegalArgumentException("User not found");
        }
        if (livestockRepository.findByFarmerId(userId).isEmpty()) {
            throw new IllegalArgumentException("User with this id does not have any livestock");
        }
        if (livestockRepository.findById(id).isEmpty()) {
            throw new IllegalArgumentException("Livestock not found");
        }
        Livestock livestock = livestockRepository.findById(id).get();
        deleteImage(livestock.getImageUrls());
        livestockRepository.deleteById(id);
    }

    private void deleteImage(String imageUrl) throws IOException {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            String filename = imageUrl.substring(imageUrl.lastIndexOf('/') + 1);
            Path filePath = Paths.get(uploadDir, filename);
            Files.deleteIfExists(filePath);
        }
    }
}