package com.example.iam1.service;

import com.example.iam1.model.JwtInfo;
import com.example.iam1.model.RedisToken;
import com.example.iam1.model.TokenPayload;
import com.example.iam1.model.dto.UserDTO;
import com.example.iam1.model.request.LoginRequest;
import com.example.iam1.repository.RedisTokenRepository;
import com.example.iam1.security.CustomUserDetails;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import com.example.iam1.model.response.LoginResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;

@Service
@Slf4j
public class AuthenticationService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private RedisTokenRepository redisTokenRepository;

    @Autowired
    private UserDetailServiceCustome userDetailsService;

    public LoginResponse login(LoginRequest request){
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
        Authentication authentication = authenticationManager.authenticate(token);

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        //trả về token
        TokenPayload accessPayload = jwtService.generateAccessToken(customUserDetails);
        TokenPayload freshPayload = jwtService.generateRefreshToken(customUserDetails);

        redisTokenRepository.save(RedisToken.builder()
                        .jwtID(freshPayload.getJwtID())
                        .expriredTime(freshPayload.getExpiredTime().getTime())
                        .build());
        return LoginResponse.builder()
                .accessToken(accessPayload.getToken())
                .refreshToken(freshPayload.getToken())
                .build();
    }

    public void logout(String accessToken, String refreshToken) throws ParseException {
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

    public LoginResponse refreshToken(String refreshToken) {
        try {
            if (jwtService.checkRefreshToken(refreshToken)) {
                String email = jwtService.getSubject(refreshToken);
                CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(email);
                TokenPayload newAccessToken = jwtService.generateAccessToken(userDetails);

                return LoginResponse.builder()
                        .accessToken(newAccessToken.getToken())
                        .refreshToken(refreshToken)
                        .build();
            }
        } catch (Exception e) {
            throw new RuntimeException("Refresh token invalid: " + e.getMessage());
        }
        return null;
    }

}
