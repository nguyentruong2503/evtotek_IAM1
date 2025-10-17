package com.example.iam1.service;

import com.example.iam1.model.dto.PasswordDTO;
import com.example.iam1.model.dto.UserDTO;
import com.example.iam1.model.response.UserProfile;

public interface UserService {
    UserDTO register (UserDTO userDTO);

    UserDTO findOneByEmailAndActive(String email, boolean active);

    UserDTO updateUser(UserDTO userDTO,String token);

    UserProfile findUserById(String token);

    boolean  updatePassword(String token,String refreshToken ,PasswordDTO passwordDTO);
}
