package com.xinyu.webapp.controller;

import com.amazonaws.services.dynamodbv2.xspec.S;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.xinyu.webapp.entity.*;
import com.xinyu.webapp.error.ProductNotFoundException;
import com.xinyu.webapp.error.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
public class ImageController {
    @Autowired
    private AppUserRepository userRepository;
    @Autowired
    private AppProductRepository productRepository;
    @Autowired
    private AppImageRepository imageRepository;

    @Autowired
    private AmazonS3 s3Client;
    private final String AWS_REGION = System.getenv("AWS_REGION");
    private final String BUCKET_NAME = System.getenv("BUCKET_NAME");

    @GetMapping(path = "/v1/product/{product_id}/image")
    public ResponseEntity<List<AppImage>> GetListImages(@PathVariable(value = "product_id") int id, Authentication authentication) {
        String username = authentication.getName();
        AppUser userOri = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));

        AppProduct product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
        if (!product.getAppUser().getId().equals(userOri.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        List<AppImage> res = imageRepository.findByAppProduct(product);

        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @PostMapping(path = "/v1/product/{product_id}/image")
    public ResponseEntity<AppImage> uploadImage(@PathVariable(value = "product_id") int id, @RequestParam("file") MultipartFile file, Authentication authentication) throws IOException {
        String username = authentication.getName();
        AppUser userOri = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));

        AppProduct product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
        if (!product.getAppUser().getId().equals(userOri.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        String filename = file.getOriginalFilename();
        String fileType = file.getContentType();
        byte[] imageData = file.getBytes();
        String key = UUID.randomUUID().toString();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(fileType);
        metadata.setContentLength(imageData.length);

        PutObjectRequest put = new PutObjectRequest(BUCKET_NAME, key + "/" + filename, new ByteArrayInputStream(imageData), metadata);
        s3Client.putObject(put);


        AppImage appImage = new AppImage();
        appImage.setFile_name(filename);
        appImage.setKeyString(key);
        appImage.setAppProduct(product);
        appImage.setS3_bucket_path(s3Client.getUrl(BUCKET_NAME, key).toString());
        imageRepository.save(appImage);

        return ResponseEntity.status(HttpStatus.CREATED).body(appImage);
    }

    @GetMapping(path = "/v1/product/{product_id}/image/{image_id}")
    public ResponseEntity<AppImage> getImage(@PathVariable(value = "product_id") int product_id, @PathVariable(value = "image_id") String image_id, Authentication authentication) {
        String username = authentication.getName();
        AppUser userOri = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));

        AppProduct product = productRepository.findById(product_id).orElseThrow(() -> new ProductNotFoundException(product_id));
        if (!product.getAppUser().getId().equals(userOri.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        Optional<AppImage> optionalAppImage = imageRepository.findById(image_id);
        if (optionalAppImage.isPresent()) {
            AppImage image = optionalAppImage.get();
            return ResponseEntity.status(HttpStatus.OK).body(image);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    @DeleteMapping(path = "/v1/product/{product_id}/image/{image_id}")
    public ResponseEntity<String> deleteImage(@PathVariable(value = "product_id") int product_id, @PathVariable(value = "image_id") String image_id, Authentication authentication) {
        String username = authentication.getName();
        AppUser userOri = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));

        AppProduct product = productRepository.findById(product_id).orElseThrow(() -> new ProductNotFoundException(product_id));
        if (!product.getAppUser().getId().equals(userOri.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        Optional<AppImage> optionalAppImage = imageRepository.findById(image_id);
        if (optionalAppImage.isPresent()) {
            AppImage image = optionalAppImage.get();
            String path = image.getKeyString();
            String filename = image.getFile_name();
            imageRepository.deleteById(image.getId());
            s3Client.deleteObject(BUCKET_NAME, path + '/' + filename);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
}
