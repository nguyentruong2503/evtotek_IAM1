package com.example.iam1.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.util.Date;

@Getter
@Setter
public class UserProfile {
    private String email;
    private String fullName;
    private String phone;
    private String address;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private Date birthday;
    private String avatar;
}
