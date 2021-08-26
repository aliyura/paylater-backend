package com.syrol.paylater.services;
import com.syrol.paylater.entities.User;
import com.syrol.paylater.repositories.UserRepository;
import com.syrol.paylater.util.App;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    private final App app;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        app.log(username);
        User appUser= userRepository.findByEmail(username).orElse(userRepository.findByMobile(username).orElse(null));
        app.log(appUser.getPassword());
        app.log(appUser.getEmail());
        return new  org.springframework.security.core.userdetails.User(appUser.getEmail(), appUser.getPassword(), new ArrayList<>());
    }
}
