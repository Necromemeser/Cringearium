package com.edu.cringearium.entities.user;

import com.edu.cringearium.entities.chat.Chat;
import com.edu.cringearium.entities.course.Course;
import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "user_account")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Lob
    @Column(name = "profile_pic")
    private byte[] profilePic;

    @ManyToOne
    @JoinColumn(name = "user_role_id", nullable = false)
    private UserRole userRole;

    @ManyToMany
    @JoinTable(
            name = "user_chat",
            schema = "cringearium",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "chat_id")
    )
    private Set<Chat> chats;

    @ManyToMany
    @JoinTable(
            name = "user_course",
            schema = "cringearium",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private Set<Course> courses;

    // Конструкторы
    public User() {}

    public User(String username, String email, String passwordHash, UserRole userRole) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.userRole = userRole;
    }

    public User(String username, String email, String passwordHash, byte[] profilePic, UserRole userRole) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.profilePic = profilePic;
        this.userRole = userRole;
    }



    // Геттеры
    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public byte[] getProfilePic() {
        return profilePic;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public Set<Course> getCourses() {
        return courses;
    }


    // Сеттеры
    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setProfilePic(byte[] profilePic) {
        this.profilePic = profilePic;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    public Set<Chat> getChats() {
        return chats;
    }

    public void setChats(Set<Chat> chats) {
        this.chats = chats;
    }

    public void setCourses(Set<Course> courses) {
        this.courses = courses;
    }
}
