//package com.Hinga.farmMis.Dto.Request;
//
//import com.Hinga.farmMis.Constants.LivestockStatus;
//import com.Hinga.farmMis.Model.Livestock;
//import jakarta.validation.constraints.*;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.List;
//
//@Getter
//@Setter
//@AllArgsConstructor
//@NoArgsConstructor
//class LiveStockDto {
//    private Long livestockId;
//
//    @NotBlank(message = "Type is required")
//    private String type;
//
//    @Min(value = 1, message = "Count must be at least 1")
//    private int count;
//
//    private String description;
//
//    private String breed;
//
//    @PastOrPresent(message = "Birth date cannot be in the future")
//    private LocalDate birthDate;
//
//    @Min(value = 0, message = "Price cannot be negative")
//    private double price;
//
//    @Min(value = 0, message = "Quantity cannot be negative")
//    private int quantity;
//
//    @Min(value = 0, message = "Weight cannot be negative")
//    private double weight;
//
//    @NotNull(message = "Status is required")
//    private LivestockStatus status;
//
//    @NotNull(message = "Image URLs list cannot be null")
//    private String imageUrls;
//
//    @NotNull(message = "Farmer ID is required")
//    private Long farmerId;
//
//    // Convert Livestock to LivestockDTO
//    public static LiveStockDto fromEntity(Livestock livestock) {
//        if (livestock.getFarmer() == null) {
//            throw new IllegalStateException("Livestock must have an associated farmer");
//        }
//        LiveStockDto dto = new LiveStockDto();
//        dto.setLivestockId(livestock.getLivestockId());
//        dto.setType(livestock.getType());
//        dto.setCount(livestock.getCount());
//        dto.setDescription(livestock.getDescription());
//        dto.setBreed(livestock.getBreed());
//        dto.setBirthDate(livestock.getBirthDate());
//        dto.setPrice(livestock.getPrice());
//        dto.setQuantity(livestock.getQuantity());
//        dto.setWeight(livestock.getWeight());
//        dto.setStatus(livestock.getStatus());
//        dto.setImageUrls(livestock.getImageUrls());
//        dto.setFarmerId(livestock.getFarmer().getId());
//        return dto;
//    }
//
//    // Convert LivestockDTO to Livestock
//    public Livestock toEntity() {
//        Livestock livestock = new Livestock();
//        livestock.setLivestockId(this.livestockId);
//        livestock.setType(this.type);
//        livestock.setCount(this.count);
//        livestock.setDescription(this.description);
//        livestock.setBreed(this.breed);
//        livestock.setBirthDate(this.birthDate);
//        livestock.setPrice(this.price);
//        livestock.setQuantity(this.quantity);
//        livestock.setWeight(this.weight);
//        livestock.setStatus(this.status);
//        livestock.setImageUrls(this.imageUrls);
//        return livestock;
//    }
//
//    public String getType() {
//        return type;
//    }
//
//    public void setType(String type) {
//        this.type = type;
//    }
//
//    public int getCount() {
//        return count;
//    }
//
//    public void setCount(int count) {
//        this.count = count;
//    }
//
//    public String getDescription() {
//        return description;
//    }
//
//    public void setDescription(String description) {
//        this.description = description;
//    }
//
//    public String getBreed() {
//        return breed;
//    }
//
//    public void setBreed(String breed) {
//        this.breed = breed;
//    }
//
//    public LocalDate getBirthDate() {
//        return birthDate;
//    }
//
//    public void setBirthDate(LocalDate birthDate) {
//        this.birthDate = birthDate;
//    }
//
//    public double getPrice() {
//        return price;
//    }
//
//    public void setPrice(double price) {
//        this.price = price;
//    }
//
//    public int getQuantity() {
//        return quantity;
//    }
//
//    public void setQuantity(int quantity) {
//        this.quantity = quantity;
//    }
//
//    public double getWeight() {
//        return weight;
//    }
//
//    public void setWeight(double weight) {
//        this.weight = weight;
//    }
//
//    public LivestockStatus getStatus() {
//        return status;
//    }
//
//    public void setStatus(LivestockStatus status) {
//        this.status = status;
//    }
//
//    public String getImageUrls() {
//        return imageUrls;
//    }
//
//    public void setImageUrls(String imageUrls) {
//        this.imageUrls = imageUrls;
//    }
//
//    public Long getFarmerId() {
//        return farmerId;
//    }
//
//    public void setFarmerId(Long farmerId) {
//        this.farmerId = farmerId;
//    }
//}