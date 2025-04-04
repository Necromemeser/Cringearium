package com.edu.cringearium.dto.course;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CourseDataTextDTO {
    private String content;

    // Конструкторы
    public CourseDataTextDTO() {
    }

    public CourseDataTextDTO(String content) {
        this.content = content;
    }

    // Геттеры и сеттеры
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}