package com.example.iam1.model;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
public class JwtInfo {
    private String jwtID;
    private Date issueTime;
    private Date expiredTime;
}
