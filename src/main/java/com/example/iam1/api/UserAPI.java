package com.example.iam1.api;

import com.example.iam1.model.dto.PasswordDTO;
import com.example.iam1.model.dto.UserDTO;
import com.example.iam1.model.response.UserProfile;
import com.example.iam1.service.CloudinaryService;
import com.example.iam1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/user")
public class  UserAPI{
    @Autowired
    private UserService userService;

    @Autowired
    private CloudinaryService cloudinaryService;

    @PostMapping("/register")
    public UserDTO register(@RequestBody UserDTO userDTO) {
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
}
