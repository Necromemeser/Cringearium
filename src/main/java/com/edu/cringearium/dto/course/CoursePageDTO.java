package com.edu.cringearium.dto.course;

import com.edu.cringearium.entities.course.Course;

import java.time.LocalDateTime;

public class CoursePageDTO {

    private Long id;
    private String courseName;
    private String courseTheme;

    private String courseDescription;

    private LocalDateTime createdAt;

    private byte[] courseImage;
    private Long price;
    // Только необходимые поля

    public CoursePageDTO() {}

    public CoursePageDTO(Course course) {
        this.id = course.getId();
        this.courseName = course.getCourseName();
        this.courseTheme = course.getCourseTheme();
        this.courseDescription = course.getCourseDescription();
        this.createdAt = course.getCreatedAt();
        this.courseImage = course.getCourseImage();
        this.price = course.getPrice();
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

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public String getCourseDescription() {
        return courseDescription;
    }

    public void setCourseDescription(String courseDescription) {
        this.courseDescription = courseDescription;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public byte[] getCourseImage() {
        return courseImage;
    }

    public void setCourseImage(byte[] courseImage) {
        this.courseImage = courseImage;
    }
}
