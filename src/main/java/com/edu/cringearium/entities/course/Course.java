package com.edu.cringearium.entities.course;

import com.edu.cringearium.entities.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "course")
@Getter
@Setter
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private Long id;


    @Column(name = "course_name", nullable = false, length = 45)
    private String courseName;


    @Column(name = "course_theme", nullable = false, length = 45)
    private String courseTheme;


    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();


    @Column(name = "price", nullable = false)
    private Long price;

    @Column(name = "course_description", columnDefinition = "TEXT")
    private String courseDescription;

    @Lob
    @Column(name = "course_image")
    private byte[] courseImage;

    @ManyToMany
    @JoinTable(
            name = "user_course",
            schema = "cringearium",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> users;

    public Course() {}

    public Course(Long id, String courseName, String courseTheme, LocalDateTime createdAt, Long price) {
        this.id = id;
        this.courseName = courseName;
        this.courseTheme = courseTheme;
        this.createdAt = createdAt;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getCourseTheme() {
        return courseTheme;
    }

    public Long getPrice() {
        return price;
    }

    public Set<User> getUsers() {
        return users;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getCourseDescription() {
        return courseDescription;
    }

    public byte[] getCourseImage() {
        return courseImage;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public void setCourseTheme(String courseTheme) {
        this.courseTheme = courseTheme;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }


    public void setPrice(Long price) {
        this.price = price;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public void setCourseDescription(String courseDescription) {
        this.courseDescription = courseDescription;
    }

    public void setCourseImage(byte[] courseImage) {
        this.courseImage = courseImage;
    }
}
