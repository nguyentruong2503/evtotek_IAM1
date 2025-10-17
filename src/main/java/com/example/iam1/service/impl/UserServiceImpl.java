package com.example.iam1.service.impl;

import com.example.iam1.converter.UserConverter;
import com.example.iam1.entity.RoleEntity;
import com.example.iam1.entity.UserEntity;
import com.example.iam1.exception.DuplicateEmailException;
import com.example.iam1.exception.InvalidTokenException;
import com.example.iam1.exception.InvalidUserID;
import com.example.iam1.exception.UserNotFoundException;
import com.example.iam1.model.JwtInfo;
import com.example.iam1.model.RedisToken;
import com.example.iam1.model.dto.PasswordDTO;
import com.example.iam1.model.dto.UserDTO;
import com.example.iam1.model.response.UserProfile;
import com.example.iam1.repository.RedisTokenRepository;
import com.example.iam1.repository.RoleRepository;
import com.example.iam1.repository.UserRepository;
import com.example.iam1.service.JWTService;
import com.example.iam1.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserConverter userConverter;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private JWTService jwtService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private RedisTokenRepository redisTokenRepository;

    @Override
    public UserDTO register(UserDTO userDTO) {
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new DuplicateEmailException("Email đã tồn tại");
        }
        UserEntity userEntity = modelMapper.map(userDTO,UserEntity.class);

        RoleEntity defaultRole= roleRepository.findByCode("user");
        userEntity.setRoles(Collections.singletonList(defaultRole));
        userEntity.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        userEntity.setActive(true);

        userRepository.save(userEntity);

        return userDTO;
    }

    @Override
    public UserDTO findOneByEmailAndActive(String email, boolean active) {
        return userRepository.findByEmailAndActive(email, active)
                .map(userConverter::toUserDTO)
                .orElse(null);
    }

    @Override
    public UserDTO updateUser(UserDTO userDTO, String token) {
        //lấy userID từ token
        Long userIdFromToken;
        try {
            userIdFromToken = jwtService.getUserIdFromToken(token);
        } catch (ParseException e) {
            throw new InvalidTokenException("Token không hợp lệ");
        }
        //Lấy userID từ DTO client gửi về
        Long userIDFromDTO = userDTO.getId();

        if (!userIdFromToken.equals(userIDFromDTO)) {
            throw new InvalidUserID("Bạn không có quyền sửa thông tin người khác");
        }

        if (userIDFromDTO == null) {
            throw new RuntimeException("ID người dùng trống");
        }

        // Tìm user theo ID
        UserEntity userEntity = userRepository.findById(userIDFromDTO)
                .orElseThrow(() -> new UserNotFoundException("Không tìm thấy"));

        userEntity.setFullName(userDTO.getFullName());
        userEntity.setPhone(userDTO.getPhone());
        userEntity.setAddress(userDTO.getAddress());
        userEntity.setBirthday(userDTO.getBirthday());
        userEntity.setAvatarUrl(userDTO.getAvatarUrl());
        userEntity.setActive(true);

        userRepository.save(userEntity);

        userDTO.setId(userEntity.getId());
        return userDTO;
    }

    @Override
    public UserProfile findUserById(String token) {
        Long userIdFromToken;
        try {
            userIdFromToken = jwtService.getUserIdFromToken(token);
        } catch (ParseException e) {
            throw new InvalidTokenException("Token không hợp lệ");
        }

        UserEntity userEntity = userRepository.findById(userIdFromToken)
                .orElseThrow(() -> new UserNotFoundException("Không tìm thấy"));

        UserProfile profile = new UserProfile();
        profile.setEmail(userEntity.getEmail());
        profile.setFullName(userEntity.getFullName());
        profile.setPhone(userEntity.getPhone());
        profile.setAddress(userEntity.getAddress());
        profile.setBirthday(userEntity.getBirthday());
        profile.setAvatar(userEntity.getAvatarUrl());

        return profile;
    }

    @Override
    public boolean updatePassword(String token,String refreshToken, PasswordDTO passwordDTO) {
        Long userIdFromToken;
        try {
            userIdFromToken = jwtService.getUserIdFromToken(token);
        } catch (ParseException e) {
            throw new InvalidTokenException("Token không hợp lệ");
        }

        UserEntity userEntity = userRepository.findById(userIdFromToken)
                .orElseThrow(() -> new UserNotFoundException("Không tìm thấy người dùng"));

        if (passwordEncoder.matches(passwordDTO.getCurrentPassword(), userEntity.getPassword())
                && passwordDTO.getNewPassword().equals(passwordDTO.getConfirmPassword())) {
            userEntity.setPassword(passwordEncoder.encode(passwordDTO.getNewPassword()));
            userRepository.save(userEntity);

            try {
                revokeToken(token, refreshToken);
            } catch (Exception e) {
                throw new RuntimeException("Lỗi khi vô hiệu hóa token");
            }
            return true;
        }
        return false;
    }

    public void revokeToken(String accessToken,String refreshToken) throws ParseException{
        JwtInfo accessJwtInfo = jwtService.parseToken(accessToken);
        String jwtID = accessJwtInfo.getJwtID();
        Date issueTime = accessJwtInfo.getIssueTime();
        Date expiredTime = accessJwtInfo.getExpiredTime();
        if(expiredTime.before(new Date())){
            return;
        }

        JwtInfo refreshTJwtInfo = jwtService.parseToken(refreshToken);
        String refreshjwtID = refreshTJwtInfo.getJwtID();
        Date refreshIssueTime = refreshTJwtInfo.getIssueTime();
        Date refreshExpiredTime = refreshTJwtInfo.getExpiredTime();
        if(refreshExpiredTime.before(new Date())){
            return;
        }

        //logic khi tạo là sẽ thêm refresh token vào redis để kiểm soát

        //thêm accessToken vào redis để sau vô hiệu nó đi
        RedisToken accessRedisToken = RedisToken.builder()
                .jwtID(jwtID)
                .expriredTime(expiredTime.getTime() - issueTime.getTime())
                .build();

        redisTokenRepository.save(accessRedisToken);

        //Xóa nó khỏi redis
        RedisToken refreshRedisToken = RedisToken.builder()
                .jwtID(refreshjwtID)
                .expriredTime(refreshExpiredTime.getTime() - refreshIssueTime.getTime())
                .build();

        redisTokenRepository.delete(refreshRedisToken);
    }

}
