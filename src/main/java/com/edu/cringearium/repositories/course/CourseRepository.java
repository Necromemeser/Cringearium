package com.edu.cringearium.repositories.course;

import com.edu.cringearium.entities.course.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByCourseThemeContainingIgnoreCase(String theme);
    List<Course> findByCourseNameContainingIgnoreCase(String name);
}
