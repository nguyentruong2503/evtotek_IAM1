package com.example.iam1.model.response;

import lombok.*;

@Data // sinh getter, setter, toString, equals, hashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
}
