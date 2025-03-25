package com.edu.cringearium.entities.course;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "course")
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

    public void setId(Long id) {
        this.id = id;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseTheme() {
        return courseTheme;
    }

    public void setCourseTheme(String courseTheme) {
        this.courseTheme = courseTheme;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }
}
