package com.Hinga.farmMis.controllers;

import com.Hinga.farmMis.Dto.ProductDto;
import com.Hinga.farmMis.Model.Products;
import com.Hinga.farmMis.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

//    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public String testUpload(@RequestPart String product, @RequestPart MultipartFile image) {
//        return "Received: " + product + ", Image: " + image.getOriginalFilename();
//    }
    @PostMapping(consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Products> addProduct(@RequestHeader("Authorization") String token,
                                                @ModelAttribute ProductDto productDTO,
                                                @RequestParam(value = "image", required=true) MultipartFile imageFile) throws IOException {
        // Token validation logic (similar to FarmController)
        if (!isValidToken(token)) {
            return ResponseEntity.status(401).body(null);
        }


        Products product = productService.addProduct(productDTO, imageFile);
        return ResponseEntity.ok(product);
    }

    @GetMapping
    public ResponseEntity<List<Products>> getAllProducts() {
        List<Products> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Products> getProductById(@PathVariable int id) {
        Products product = productService.getProductById(id).orElse(null);
        return ResponseEntity.ok(product);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Products> updateProduct(@PathVariable int id, @RequestBody ProductDto productDTO) throws IOException {
        Products updatedProduct = productService.updateProduct(id, productDTO);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable int id) throws IOException {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    private boolean isValidToken(String token) {
        // Implement your token validation logic here
        return true; // Placeholder for actual validation
    }
}