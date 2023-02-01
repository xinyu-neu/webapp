package com.xinyu.webapp.security.basicauth.service;

import com.xinyu.webapp.security.basicauth.entity.AppUserRepository;
import com.xinyu.webapp.security.basicauth.entity.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

  @Autowired
  private AppUserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
    Optional<AppUser> user = userRepository.findByUsername(s);
    if (user.isPresent()) {
      AppUser appuser = user.get();
      return User.withUsername(appuser.getUsername()).password(appuser.getPassword()).authorities("USER").build();
    } else {
      throw new UsernameNotFoundException(String.format("Username [%s] not found", s));
    }
  }
}
