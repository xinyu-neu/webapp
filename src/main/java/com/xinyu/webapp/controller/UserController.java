package com.xinyu.webapp.controller;

import com.xinyu.webapp.entity.AppProduct;
import com.xinyu.webapp.entity.AppProductRepository;
import com.xinyu.webapp.entity.AppUser;
import com.xinyu.webapp.entity.AppUserRepository;
import com.xinyu.webapp.error.ProductNotFoundException;
import com.xinyu.webapp.error.UserNotFoundException;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class UserController {
  @Autowired
  private AppUserRepository userRepository;

  @Autowired
  private BCryptPasswordEncoder bCryptPasswordEncoder;

  @GetMapping(path = "/healthz")
  public void checkHealth() {
    return;
  }

  @PostMapping(path = "/v1/user")
  public ResponseEntity<AppUser> addNewUser(@RequestBody AppUser user) {
    Optional<AppUser> optionalAppUser = userRepository.findByUsername(user.getUsername());
    if (optionalAppUser.isPresent()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }
    Boolean valid = EmailValidator.getInstance().isValid(user.getUsername());
    if (!valid) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
    userRepository.save(user);

    return ResponseEntity.status(HttpStatus.CREATED).body(user);
  }

  @GetMapping(path = "/v1/user/{id}")
  public ResponseEntity<AppUser> getUser(@PathVariable(value = "id") int id,
      Authentication authentication) {
    AppUser user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    if (!authentication.getName().equals(user.getUsername())) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
    }
    return ResponseEntity.status(HttpStatus.OK).body(user);
  }

  @PutMapping(path = "/v1/user/{id}")
  public ResponseEntity<String> updateUser(@PathVariable(value = "id") int id,
      @RequestBody AppUser user,
      Authentication authentication) {
    AppUser userOri = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));

    if (!userOri.getUsername().equals(authentication.getName())) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Can not update other account's information");
    }
    if (!userOri.getUsername().equals(user.getUsername())) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Can not update account's username");
    }
    userOri.setFirst_name(user.getFirst_name());
    userOri.setLast_name(user.getLast_name());
    userOri.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

    userRepository.save(userOri);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
  }

}
