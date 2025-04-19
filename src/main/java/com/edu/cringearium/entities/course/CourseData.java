package com.edu.cringearium.entities.course;

import com.edu.cringearium.entities.course.Course;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Entity
@Table(name = "course_data")
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_data_id")
    private Long id;

    @Lob
    @Column(name = "content", nullable = false, columnDefinition = "oid")
    private byte[] content;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }
}
