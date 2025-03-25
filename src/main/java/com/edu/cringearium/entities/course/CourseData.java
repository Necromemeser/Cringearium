package com.edu.cringearium.entities.course;

import com.edu.cringearium.entities.course.Course;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "course_data")
@Getter
@Setter
@NoArgsConstructor
public class CourseData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_data_id")
    private Long id;

    @Lob
    @Column(name = "content", nullable = false)
    private byte[] content;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    public CourseData(Long id, byte[] content, Course course) {
        this.id = id;
        this.content = content;
        this.course = course;
    }

}
