package com.xinyu.webapp.security.providers;

import com.xinyu.webapp.security.basicauth.entity.AppUserRepository;
import com.xinyu.webapp.security.basicauth.entity.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserIdentityAuthenticationProvider
        implements AuthenticationProvider {

  @Autowired
  private AppUserRepository userRepository;
  @Autowired
  private BCryptPasswordEncoder bCryptPasswordEncoder;

  UserDetails isValidUser(String username, String password) {
    Optional<AppUser> optionalAppUser = userRepository.findByUsername(username);
    if(optionalAppUser.isPresent()) {
      AppUser user = optionalAppUser.get();
      BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
      if(!encoder.matches(password, user.getPassword())) return null;
      else {
        UserDetails userDetails = User.withUsername(username).password(password).roles("USER_ROLE").build();
        return userDetails;
      }
    } else {
      return null;
    }
  }

  @Override
  public Authentication authenticate(Authentication authentication) {
    String username = authentication.getName();
    String password = authentication.getCredentials().toString();

    UserDetails userDetails = isValidUser(username, password);

    if (userDetails != null) {
      return new UsernamePasswordAuthenticationToken(
              username,
              password,
              userDetails.getAuthorities());
    } else {
      throw new BadCredentialsException("Incorrect user credentials !!");
    }
  }

  @Override
  public boolean supports(Class<?> authenticationType) {
    return authenticationType
            .equals(UsernamePasswordAuthenticationToken.class);
  }
}