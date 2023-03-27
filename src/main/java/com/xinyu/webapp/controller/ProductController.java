package com.xinyu.webapp.controller;

import com.timgroup.statsd.StatsDClient;
import com.xinyu.webapp.entity.AppProduct;
import com.xinyu.webapp.entity.AppProductRepository;
import com.xinyu.webapp.entity.AppUser;
import com.xinyu.webapp.entity.AppUserRepository;
import com.xinyu.webapp.error.ProductNotFoundException;
import com.xinyu.webapp.error.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final static Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private StatsDClient statsDClient;

    @PostMapping(path = "/v1/product")
    public ResponseEntity<AppProduct> addProduct(@RequestBody AppProduct appProduct, Authentication authentication) {
        statsDClient.incrementCounter("endpoint.product.http.post");

        if (appProduct.getQuantity() < 0 || appProduct.getQuantity() > 100) {
            logger.error("this is a error message. product quantity {} is invalid", appProduct.getQuantity());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        String username = authentication.getName();
        AppUser userOri = userRepository.findByUsername(username).orElseThrow(() -> {
            logger.error("this is a error message. user with username {} is not found.", username);
            return new UserNotFoundException(username);
        });

        if (appProduct.getName() == null || appProduct.getDescription() == null || appProduct.getSku() == null
                || appProduct.getManufacturer() == null || appProduct.getQuantity() == null) {
            logger.error("this is a error message. product is invalid");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        Optional<AppProduct> optinalProduct = productRepository.findBySku(appProduct.getSku());
        if (optinalProduct.isPresent()) {
            logger.error("this is a error message. product with sku {} already exits", appProduct.getSku());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        appProduct.setAppUser(userOri);
        productRepository.save(appProduct);

        logger.info("this is a info message. product created");
        return ResponseEntity.status(HttpStatus.CREATED).body(appProduct);
    }

    @GetMapping(path = "/v1/product/{id}")
    public ResponseEntity<AppProduct> getProduct(@PathVariable(value = "id") int id) {
        statsDClient.incrementCounter("endpoint.product.http.get");

        AppProduct product = productRepository.findById(id).orElseThrow(() -> {
            logger.info("this is a info message. product with id {} not found", id);
            return new ProductNotFoundException(id);
        });

        logger.info("this is a info message. get product is ok");
        return ResponseEntity.status(HttpStatus.OK).body(product);
    }

    @PutMapping(path = "/v1/product/{id}")
    public ResponseEntity<String> updateProduct(@PathVariable(value = "id") int id, Authentication authentication, @RequestBody AppProduct appProduct) {
        statsDClient.incrementCounter("endpoint.product.http.put");

        AppProduct product = productRepository.findById(id).orElseThrow(() -> {
            logger.error("this is a error message. product with id {} is not found.", id);
            return new ProductNotFoundException(id);
        });

        String username = authentication.getName();
        AppUser userOri = userRepository.findByUsername(username).orElseThrow(() -> {
            logger.error("this is a error message. user with username {} is not found.", username);
            return new UserNotFoundException(username);
        });

        if (!product.getAppUser().getId().equals(userOri.getId())) {
            logger.error("this is a error message. auth invalid");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        if (appProduct.getName() == null || appProduct.getDescription() == null || appProduct.getSku() == null
                || appProduct.getManufacturer() == null || appProduct.getQuantity() == null
                || (appProduct.getQuantity() < 0 || appProduct.getQuantity() > 100)) {
            logger.error("this is a error message. product is invalid");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        product.setName(appProduct.getName());
        product.setDescription(appProduct.getDescription());
        product.setSku(appProduct.getSku());
        product.setManufacturer(appProduct.getManufacturer());
        product.setQuantity(appProduct.getQuantity());
        productRepository.save(product);

        logger.info("this is a info message. update product is ok");
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

    @PatchMapping(path = "/v1/product/{id}")
    public ResponseEntity<String> updateProduct2(@PathVariable(value = "id") int id, Authentication authentication, @RequestBody AppProduct appProduct) {
        statsDClient.incrementCounter("endpoint.product.http.patch");

        AppProduct product = productRepository.findById(id).orElseThrow(() -> {
            logger.error("this is a error message. product with id {} is not found.", id);
            return new ProductNotFoundException(id);
        });

        String username = authentication.getName();
        AppUser userOri = userRepository.findByUsername(username).orElseThrow(() -> {
            logger.error("this is a error message. user with username {} is not found.", username);
            return new UserNotFoundException(username);
        });

        if (!product.getAppUser().getId().equals(userOri.getId())) {
            logger.error("this is a error message. auth invalid");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        if (appProduct.getQuantity() != null && (appProduct.getQuantity() < 0 || appProduct.getQuantity() > 100)) {
            logger.error("this is a error message. product is invalid");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        if (appProduct.getSku() != null) {
            Optional<AppProduct> optinalProduct = productRepository.findBySku(appProduct.getSku());
            if (optinalProduct.isPresent()) {
                logger.error("this is a error message. product with sku {} already exits", appProduct.getSku());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
        }

        if (appProduct.getName() != null) {product.setName(appProduct.getName());}
        if (appProduct.getDescription() != null) {product.setDescription(appProduct.getDescription());}
        if (appProduct.getSku() != null) {product.setSku(appProduct.getSku());}
        if (appProduct.getManufacturer() != null) {product.setManufacturer(appProduct.getManufacturer());}
        if (appProduct.getQuantity() != null) {product.setQuantity(appProduct.getQuantity());}
        productRepository.save(product);

        logger.info("this is a info message. update product is ok");
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

    @DeleteMapping(path = "/v1/product/")
    public ResponseEntity<String> deleteWithoutId() {
        statsDClient.incrementCounter("endpoint.product.http.delete");

        logger.error("this is a error message. can not find product without id");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    @DeleteMapping(path = "/v1/product/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable(value = "id") int id, Authentication authentication) {
        statsDClient.incrementCounter("endpoint.product.http.delete");

        AppProduct product = productRepository.findById(id).orElseThrow(() -> {
            logger.error("this is a error message. product with id {} is not found.", id);
            return new ProductNotFoundException(id);
        });

        String username = authentication.getName();
        AppUser userOri = userRepository.findByUsername(username).orElseThrow(() -> {
            logger.error("this is a error message. user with username {} is not found.", username);
            return new UserNotFoundException(username);
        });

        if (!product.getAppUser().getId().equals(userOri.getId())) {
            logger.error("this is a error message. auth invalid");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        productRepository.deleteById(id);

        logger.info("this is a info message. delete product with id {} is ok", id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }
}






























