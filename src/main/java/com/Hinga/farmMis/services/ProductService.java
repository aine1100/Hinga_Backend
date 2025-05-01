//package com.Hinga.farmMis.services;
//
//import com.Hinga.farmMis.Constants.ProductCategory;
//import com.Hinga.farmMis.Dto.ProductDto;
//import com.Hinga.farmMis.Model.Products;
//import com.Hinga.farmMis.repository.ProductRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.nio.file.StandardCopyOption;
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//
//@Service
//public class ProductService {
//
//    private final ProductRepository productRepository;
//    private final String uploadDir;
//
//    @Autowired
//    public ProductService(ProductRepository productRepository,
//                          @Value("${file.upload-dir}") String uploadDir) {
//        this.productRepository = productRepository;
//        this.uploadDir = uploadDir;
//    }
//
//    public Products addProduct(ProductDto productDTO, MultipartFile imageFile) throws IOException {
//        // Validate input
//        if (imageFile == null || imageFile.isEmpty()) {
//            throw new IllegalArgumentException("Image file is required");
//        }
//
//        // Create product entity
//        Products product = new Products();
//        product.setName(productDTO.getProName());
//        product.setDescription(productDTO.getProDesc());
//        product.setPrice(productDTO.getProPrice());
//        product.setUnit(productDTO.getProUnits());
//        product.setCategory(productDTO.getProCategory());
//        product.setOwner_email(productDTO.getOwnerEmail());
//
//        // Handle image upload
//        System.out.println(product.toString());
//        String imageUrl = saveImage(imageFile);
//        product.setImage(imageUrl);
//
//        return productRepository.save(product);
//    }
//
//    private String saveImage(MultipartFile imageFile) throws IOException {
//        // Ensure upload directory exists
//        Path uploadPath = Paths.get(uploadDir);
//        if (!Files.exists(uploadPath)) {
//            Files.createDirectories(uploadPath);
//        }
//
//        // Generate unique filename
//        String originalFilename = imageFile.getOriginalFilename();
//        String fileExtension = originalFilename != null ?
//                originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
//        String uniqueFilename = UUID.randomUUID() + fileExtension;
//
//        // Save file
//        Path filePath = uploadPath.resolve(uniqueFilename);
//        Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
//
//        // Return relative path for web access
//        return "/uploads/" + uniqueFilename;
//    }
//
//    public List<Products> getAllProducts() {
//        return productRepository.findAll();
//    }
//
//    public Optional<Products> getProductById(int id) {
//        return productRepository.findById(id);
//    }
//
//    public Optional<Products> getProductByName(String name) {
//        return Optional.ofNullable(productRepository.findByName(name));
//    }
//
//    public Optional<List<Products>> getProductByCategory(ProductCategory category){
//        return Optional.ofNullable(productRepository.findByCategory(category));
//    }
//
//    public Optional<List<Products>> getProductByOwner_email(String owner_email){
//        return Optional.ofNullable(productRepository.findByEmail(owner_email));
//    }
//
//    public Products updateProduct(int id, ProductDto productDTO) throws IOException {
//        Products product = productRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
//
//        // Update fields
//        product.setName(productDTO.getProName());
//        product.setDescription(productDTO.getProDesc());
//        product.setPrice(productDTO.getProPrice());
//        product.setUnit(productDTO.getProUnits());
//        product.setCategory(productDTO.getProCategory());
//
//        // Update image if new one is provided
////        if (product.getImage() != null && !product.getImage().isEmpty()) {
////            // Delete old image
////            deleteImage(product.getImage());
////            // Save new image
////            String newImageUrl = saveImage(product.getImage());
////            product.setImage(newImageUrl);
////        }
//
//        return productRepository.save(product);
//    }
//
//    public void deleteProduct(int id) throws IOException {
//        Products product = productRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
//
//        // Delete associated image
//        deleteImage(product.getImage());
//
//        productRepository.deleteById(id);
//    }
//
//    private void deleteImage(String imageUrl) throws IOException {
//        if (imageUrl != null && !imageUrl.isEmpty()) {
//            String filename = imageUrl.substring(imageUrl.lastIndexOf('/') + 1);
//            Path filePath = Paths.get(uploadDir, filename);
//            Files.deleteIfExists(filePath);
//        }
//    }
//}