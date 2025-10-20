package com.example.iam1.controller;

import com.example.iam1.model.dto.PasswordDTO;
import com.example.iam1.model.dto.UserDTO;
import com.example.iam1.model.request.VerifyOtpRequest;
import com.example.iam1.model.response.UserProfile;
import com.example.iam1.service.CloudinaryService;
import com.example.iam1.service.UserService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private CloudinaryService cloudinaryService;

    @PostMapping("/register")
    public UserDTO register(@RequestBody UserDTO userDTO) throws MessagingException {
        return userService.register(userDTO);
    }

    @PutMapping()
    public UserDTO updateUser(@RequestBody UserDTO userDTO,
                              @RequestHeader("Authorization") String authToken) throws ParseException {
        String token = authToken.replace("Bearer ", "");
        return userService.updateUser(userDTO,token);
    }

    @GetMapping("/profile")
    public UserProfile getMyProfile(@RequestHeader("Authorization") String authToken) {
        String token = authToken.replace("Bearer ", "");
        return userService.findUserById(token);
    }

    @PutMapping("/change-password")
    public ResponseEntity<String> changePasswordUser(@RequestHeader("Authorization") String authToken,
                                                     @RequestHeader("Refresh-Token") String refreshToken,
                                                    @RequestBody PasswordDTO passwordDTO) {
        String token = authToken.replace("Bearer ", "");
        boolean result = userService.updatePassword(token,refreshToken, passwordDTO);
        if (result) {
            return ResponseEntity.ok("Đổi mật khẩu thành công");
        } else {
            return ResponseEntity.badRequest().body("Đổi mật khẩu thất bại. Vui lòng kiểm tra lại!");
        }
    }

    @PostMapping("/upload-img")
    public String upload(@RequestParam("file") MultipartFile file) throws Exception {
        String url = cloudinaryService.uploadImage(file);
        return url;
    }

    @PostMapping("/forgot-pass")
    public String sendOtp(@RequestHeader("Authorization") String authToken)
            throws ParseException, MessagingException {
        String token = authToken.replace("Bearer ", "");
        userService.sendOtp(token);
        return "OTP đã được gửi tới email của bạn.";
    }

    @PostMapping("/verifyOTP")
    public String verifyOtpAndChangePassword(
            @RequestHeader("Authorization") String authToken,
            @RequestHeader("Refresh-Token") String refreshToken,
            @RequestBody VerifyOtpRequest verifyOtpRequest
    ) throws ParseException {
        String token = authToken.replace("Bearer ", "");
        boolean result = userService.verifyOtpAndChangePassword(token, refreshToken, verifyOtpRequest);
        if (result) {
            return "Đổi mật khẩu thành công!";
        } else {
            return "OTP không hợp lệ hoặc đã hết hạn!";
        }
    }
}
