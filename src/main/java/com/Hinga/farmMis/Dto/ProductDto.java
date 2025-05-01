//package com.Hinga.farmMis.Dto;
//
//import com.Hinga.farmMis.Constants.ProductCategory;
//import com.Hinga.farmMis.Constants.ProductUnits;
//import com.fasterxml.jackson.annotation.JsonProperty;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//
//public class ProductDto {
//    private int id;
//    @JsonProperty("name")  // Explicitly maps to "name" in form-data
//    private String name;
//
//    @JsonProperty("description")
//    private String description;
//
//    @JsonProperty("price")
//    private double price;
//
//    @JsonProperty("unit")
//    private ProductUnits unit;
//
//    @JsonProperty("category")
//    private ProductCategory category;
//    private String imageUrl; // Field for product image
//
//    private String owner_email;
//
//    public ProductUnits getProUnits() {
//        return unit;
//    }
//
//    public void setProUnits(ProductUnits unit) {
//        this.unit = unit;
//    }
//
//    public ProductCategory getProCategory() {
//        return category;
//    }
//
//    public void setProCategory(ProductCategory category) {
//        this.category = category;
//    }
//
//    public double getProPrice() {
//        return price;
//    }
//
//    public void setProPrice(double price) {
//        this.price = price;
//    }
//
//    public String getProDesc() {
//        return description;
//    }
//
//    public void setProDesc(String description) {
//        this.description = description;
//    }
//
//    public String getProName() {
//        return name;
//    }
//
//    public void setProName(String proName) {
//        this.name = proName;
//    }
//
//    public String getImageUrl() {
//        return imageUrl;
//    }
//
//    public String getOwnerEmail() {
//        return owner_email;
//    }
//
//    public void setOwnerEmail(String owner_email) {
//        this.owner_email = owner_email;
//    }
//
//    public void setImageUrl(String imageUrl) {
//        this.imageUrl = imageUrl;
//    }
//}
