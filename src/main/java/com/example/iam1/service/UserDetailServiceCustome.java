package com.example.iam1.service;

import com.example.iam1.converter.UserConverter;
import com.example.iam1.entity.UserEntity;
import com.example.iam1.model.dto.UserDTO;
import com.example.iam1.repository.UserRepository;
import com.example.iam1.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailServiceCustome implements UserDetailsService{

    @Autowired
    UserConverter userConverter;

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity entity = userRepository.findByEmailAndActive(username, true)
                .orElseThrow(() -> new UsernameNotFoundException("User không tồn tại"));

        UserDTO dto = userConverter.toUserDTO(entity);
        return new CustomUserDetails(dto);
    }
}
