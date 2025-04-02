package com.edu.cringearium.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Lob;

public class UserDTO {
    @JsonProperty("username")
    private String username;

    @JsonProperty("email")
    private String email;

    @JsonProperty("password_hash")
    private String passwordHash;


    @Lob
    @JsonProperty("profile_pic")
    private byte[] profilePic;

    @JsonProperty("user_role_id")
    private Long userRoleId;

    // Конструкторы
    public UserDTO() {}

    public UserDTO(String username, String email, String passwordHash, byte[] profilePic, Long userRoleId) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.profilePic = profilePic;
        this.userRoleId = userRoleId;
    }

    // Геттеры и сеттеры
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public byte[] getProfilePic() { return profilePic; }
    public void setProfilePic(byte[] profilePic) { this.profilePic = profilePic; }

    public Long getUserRoleId() { return userRoleId; }
    public void setUserRoleId(Long userRoleId) { this.userRoleId = userRoleId; }
}
