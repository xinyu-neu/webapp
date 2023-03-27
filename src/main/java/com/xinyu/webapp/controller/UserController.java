package com.xinyu.webapp.controller;

import com.timgroup.statsd.StatsDClient;
import com.xinyu.webapp.entity.AppUserRepository;
import com.xinyu.webapp.entity.AppUser;
import com.xinyu.webapp.error.UserNotFoundException;
import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

  private final static Logger logger = LoggerFactory.getLogger(UserController.class);

  @Autowired
  private StatsDClient statsDClient;

  @GetMapping(path = "/healthz")
  public void checkHealth() {
    logger.info("this is a info message. get healthz is ok");
    statsDClient.incrementCounter("endpoint.healthz.http.get");
    return;
  }

  @PostMapping(path = "/v1/user")
  public ResponseEntity<AppUser> addNewUser(@RequestBody AppUser user) {
    statsDClient.incrementCounter("endpoint.user.http.post");

    Optional<AppUser> optionalAppUser = userRepository.findByUsername(user.getUsername());
    if (optionalAppUser.isPresent()) {
      logger.error("this is a error message. User already exits");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    Boolean valid = EmailValidator.getInstance().isValid(user.getUsername());
    if (!valid) {
      logger.error("this is a error message. username is not valid");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
    userRepository.save(user);

    logger.info("this is a info message. User created");
    return ResponseEntity.status(HttpStatus.CREATED).body(user);
  }

  @GetMapping(path = "/v1/user/{id}")
  public ResponseEntity<AppUser> getUser(@PathVariable(value = "id") int id,
      Authentication authentication) {
    statsDClient.incrementCounter("endpoint.user.http.get");

    AppUser user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    if (!authentication.getName().equals(user.getUsername())) {
      logger.error("this is a error message. auth invalid");
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
    }
    logger.info("this is a info message. get user is ok");
    return ResponseEntity.status(HttpStatus.OK).body(user);
  }

  @PutMapping(path = "/v1/user/{id}")
  public ResponseEntity<String> updateUser(@PathVariable(value = "id") int id,
      @RequestBody AppUser user,
      Authentication authentication) {
    statsDClient.incrementCounter("endpoint.user.http.put");

    AppUser userOri = userRepository.findById(id).orElseThrow(() -> {
      logger.error("this is a error message. can not find user with id {}", id);
      return new UserNotFoundException(id);
    });

    if (!userOri.getUsername().equals(authentication.getName())) {
      logger.error("this is a error message. can not update other account's information");
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Can not update other account's information");
    }
    if (!userOri.getUsername().equals(user.getUsername())) {
      logger.error("this is a error message. can not update account's username");
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Can not update account's username");
    }
    userOri.setFirst_name(user.getFirst_name());
    userOri.setLast_name(user.getLast_name());
    userOri.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

    userRepository.save(userOri);
    logger.info("this is info message. update user successfully");
    return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
  }

}
