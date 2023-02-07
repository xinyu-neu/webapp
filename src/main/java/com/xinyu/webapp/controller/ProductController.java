package com.xinyu.webapp.controller;

import com.xinyu.webapp.entity.AppProduct;
import com.xinyu.webapp.entity.AppProductRepository;
import com.xinyu.webapp.entity.AppUser;
import com.xinyu.webapp.entity.AppUserRepository;
import com.xinyu.webapp.error.ProductNotFoundException;
import com.xinyu.webapp.error.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class ProductController {

    @Autowired
    private AppUserRepository userRepository;
    @Autowired
    private AppProductRepository productRepository;

    @PostMapping(path = "/v1/product")
    public ResponseEntity<AppProduct> addProduct(@RequestBody AppProduct appProduct, Authentication authentication) {
        if (appProduct.getQuantity() < 0 || appProduct.getQuantity() > 100) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        String username = authentication.getName();
        AppUser userOri = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));

        if (appProduct.getName() == null || appProduct.getDescription() == null || appProduct.getSku() == null
                || appProduct.getManufacturer() == null || appProduct.getQuantity() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        Optional<AppProduct> optinalProduct = productRepository.findBySku(appProduct.getSku());
        if (optinalProduct.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        appProduct.setAppUser(userOri);
        productRepository.save(appProduct);

        return ResponseEntity.status(HttpStatus.CREATED).body(appProduct);
    }

    @GetMapping(path = "/v1/product/{id}")
    public ResponseEntity<AppProduct> getProduct(@PathVariable(value = "id") int id) {
        AppProduct product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));

        return ResponseEntity.status(HttpStatus.OK).body(product);
    }

    @PutMapping(path = "/v1/product/{id}")
    public ResponseEntity<String> updateProduct(@PathVariable(value = "id") int id, Authentication authentication, @RequestBody AppProduct appProduct) {
        AppProduct product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));

        String username = authentication.getName();
        AppUser userOri = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));

        if (!product.getAppUser().getId().equals(userOri.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        if (appProduct.getName() == null || appProduct.getDescription() == null || appProduct.getSku() == null
                || appProduct.getManufacturer() == null || appProduct.getQuantity() == null
                || (appProduct.getQuantity() < 0 || appProduct.getQuantity() > 100)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        product.setName(appProduct.getName());
        product.setDescription(appProduct.getDescription());
        product.setSku(appProduct.getSku());
        product.setManufacturer(appProduct.getManufacturer());
        product.setQuantity(appProduct.getQuantity());
        productRepository.save(product);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

    @PatchMapping(path = "/v1/product/{id}")
    public ResponseEntity<String> updateProduct2(@PathVariable(value = "id") int id, Authentication authentication, @RequestBody AppProduct appProduct) {
        AppProduct product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));

        String username = authentication.getName();
        AppUser userOri = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));

        if (!product.getAppUser().getId().equals(userOri.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        if (appProduct.getName() == null || appProduct.getDescription() == null || appProduct.getSku() == null
                || appProduct.getManufacturer() == null || appProduct.getQuantity() == null
                || (appProduct.getQuantity() < 0 || appProduct.getQuantity() > 100)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        product.setName(appProduct.getName());
        product.setDescription(appProduct.getDescription());
        product.setSku(appProduct.getSku());
        product.setManufacturer(appProduct.getManufacturer());
        product.setQuantity(appProduct.getQuantity());
        productRepository.save(product);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

    @DeleteMapping(path = "/v1/product/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable(value = "id") int id, Authentication authentication) {
        AppProduct product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));

        String username = authentication.getName();
        AppUser userOri = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));

        if (!product.getAppUser().getId().equals(userOri.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        productRepository.deleteById(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }
}






























