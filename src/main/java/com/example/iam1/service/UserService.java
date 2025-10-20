package com.example.iam1.service;

import com.example.iam1.model.dto.PasswordDTO;
import com.example.iam1.model.dto.UserDTO;
import com.example.iam1.model.request.VerifyOtpRequest;
import com.example.iam1.model.response.UserProfile;
import jakarta.mail.MessagingException;

import java.text.ParseException;

public interface UserService {
    UserDTO register (UserDTO userDTO) throws MessagingException;

    UserDTO findOneByEmailAndActive(String email, boolean active);

    UserDTO updateUser(UserDTO userDTO,String token);

    UserProfile findUserById(String token);

    boolean  updatePassword(String token,String refreshToken ,PasswordDTO passwordDTO);

    void sendOtp(String token) throws ParseException, MessagingException;

    boolean verifyOtpAndChangePassword(String token, String refreshToken, VerifyOtpRequest verifyOtpRequest);
}
