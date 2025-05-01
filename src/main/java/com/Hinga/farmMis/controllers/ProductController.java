//package com.Hinga.farmMis.controllers;
//
//import com.Hinga.farmMis.Constants.ProductCategory;
//import com.Hinga.farmMis.Constants.ProductUnits;
//import com.Hinga.farmMis.Dto.ProductDto;
//import com.Hinga.farmMis.Model.Products;
//import com.Hinga.farmMis.services.JwtService;
//import com.Hinga.farmMis.services.ProductService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.Optional;
//
//@RestController
//@RequestMapping("/api/products")
//@CrossOrigin(origins = "http://localhost:5173")
//public class ProductController {
//
//    @Autowired
//    private ProductService productService;
//    @Autowired
//    private JwtService jwtService;
//
//    //    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
////    public String testUpload(@RequestPart String product, @RequestPart MultipartFile image) {
////        return "Received: " + product + ", Image: " + image.getOriginalFilename();
////    }
//    @PostMapping(consumes=MediaType.MULTIPART_FORM_DATA_VALUE, path = "/add")
//    public ResponseEntity<?> addProduct(@RequestHeader("Authorization") String token,
//                                         @RequestParam("name") String name,
//                                         @RequestParam("description") String description,
//                                         @RequestParam("price") double price,
//                                         @RequestParam("unit") String unit,
//                                         @RequestParam("category") String category,
//                                         @RequestParam(value = "image", required=true) MultipartFile imageFile) throws IOException {
//        try {
//            if(jwtService.isTokenInvalidated(token)){
//                return  ResponseEntity.status(404).body("Invalid token");
//            }
//            // Token validation logic
//            if (!isValidToken(token)) {
//                return ResponseEntity.status(401).body("Invalid token");
//            }
//
//            String jwtToken = token.substring(7);
//            String username = jwtService.extractUsername(jwtToken);
//
//            if (username == null) {
//                return ResponseEntity.status(401).body("Invalid token");
//            }
//
//            // Convert string to enum
//            ProductUnits productUnit = ProductUnits.valueOf(unit.toUpperCase());
//            ProductCategory productCategory = ProductCategory.valueOf(category.toUpperCase());
//
//            ProductDto productDTO = new ProductDto();
//            productDTO.setProName(name);
//            productDTO.setProDesc(description);
//            productDTO.setProPrice(price);
//            productDTO.setProUnits(productUnit);
//            productDTO.setProCategory(productCategory);
//            productDTO.setOwnerEmail(username);
//
//            Products product = productService.addProduct(productDTO, imageFile);
////           return ResponseEntity.status(201).body(product);
//            return ResponseEntity.ok(product);
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.badRequest().body("Invalid unit or category value");
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
//        }
//    }
//
//    @GetMapping("/all")
//    public ResponseEntity<List<Products>> getAllProducts() {
//        List<Products> products = productService.getAllProducts();
//        return ResponseEntity.ok(products);
//    }
//    @GetMapping("/name")
//    public ResponseEntity<Products> getProductByName(@RequestParam("name") String name){
//        Optional<Products> products=productService.getProductByName(name);
//        return ResponseEntity.ok(products.get());
//    }
//
//    @GetMapping("/category")
//    public ResponseEntity<List<Products>> getProductsByCategory(@RequestParam("category") ProductCategory category){
//        Optional<List<Products>> products=productService.getProductByCategory(category);
//        return ResponseEntity.ok(products.get());
//    }
//
//    @GetMapping("/owner")
//    public ResponseEntity<List<Products>> getProductsByOwner(@RequestHeader("Authorization") String token){
//        if (!isValidToken(token)) {
//            return ResponseEntity.status(401).body(null);
//        }
//
//        String jwtToken = token.substring(7);
//        String username = jwtService.extractUsername(jwtToken);
//
//        Optional<List<Products>> products=productService.getProductByOwner_email(username);
//        return ResponseEntity.ok(products.get());
//
//    }
//
//
//
//    @GetMapping("/{id}")
//    public ResponseEntity<Products> getProductById(@PathVariable int id) {
//        Products product = productService.getProductById(id).orElse(null);
//        return ResponseEntity.ok(product);
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<Products> updateProduct(@PathVariable int id, @RequestBody ProductDto productDTO) throws IOException {
//        Products updatedProduct = productService.updateProduct(id, productDTO);
//        return ResponseEntity.ok(updatedProduct);
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteProduct(@PathVariable int id) throws IOException {
//        productService.deleteProduct(id);
//        return ResponseEntity.noContent().build();
//    }
//
//    private boolean isValidToken(String token) {
//        try {
//            String jwtToken = token.substring(7);
//            String username = jwtService.extractUsername(jwtToken);
//            return username != null;
//        } catch (Exception e) {
//            return false;
//        }
//    }
//}