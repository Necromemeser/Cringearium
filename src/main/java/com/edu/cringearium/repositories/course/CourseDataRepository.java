package com.edu.cringearium.repositories.course;

import com.edu.cringearium.entities.course.CourseData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseDataRepository extends JpaRepository<CourseData, Long> {
}
