package com.example.iam1.model.dto;

import com.example.iam1.model.dto.RoleDTO;

import java.util.Date;
import java.util.List;

public class UserDTO {
    private Long id;
    private String email;
    private String fullName;
    private String password;
    private String phone;
    private String address;
    private Date birthday;
    private String avatarUrl;
    private Boolean active;

    List<RoleDTO> roleDTOList;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Date getBirthday() { return birthday; }
    public void setBirthday(Date birthday) { this.birthday = birthday; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public Boolean  getActive() { return active; }
    public void setActive(Boolean  active) { this.active = active; }

    public List<RoleDTO> getRoleDTOList() {
        return roleDTOList;
    }

    public void setRoleDTOList(List<RoleDTO> roleDTOList) {
        this.roleDTOList = roleDTOList;
    }
}
