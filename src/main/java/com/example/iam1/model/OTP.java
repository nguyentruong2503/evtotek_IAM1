package com.example.iam1.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("OTP")
@Builder
public class OTP {
    @Id
    private String email;
    private String otp;
    @TimeToLive
    private long ttl;
}
