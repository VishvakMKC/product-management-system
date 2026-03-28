package com.vish.pms.service.serviceimpl;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.vish.pms.config.CustomUserDetails;
import com.vish.pms.entity.User;
import com.vish.pms.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService  {
    
    private final UserRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // TODO Auto-generated method stub

        User user = userRepository.findByEmail(email)
                                    .orElseThrow(() -> new UsernameNotFoundException("User not foudn"));

        return new CustomUserDetails(user);
    }
    
    

}
