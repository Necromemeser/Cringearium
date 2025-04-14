package com.edu.cringearium.repositories.course;

import com.edu.cringearium.entities.course.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByCourseThemeContainingIgnoreCase(String theme);
    List<Course> findByCourseNameContainingIgnoreCase(String name);

    @Query("SELECT c.price FROM Course c WHERE c.id = :courseId")
    Long findPriceById(@Param("courseId") Long courseId);

    // Получаем название курса по ID
    @Query("SELECT c.courseName FROM Course c WHERE c.id = :courseId")
    String findCourseNameById(@Param("courseId") Long courseId);
}
