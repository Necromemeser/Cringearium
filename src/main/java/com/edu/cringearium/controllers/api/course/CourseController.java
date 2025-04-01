package com.edu.cringearium.controllers.api.course;

import com.edu.cringearium.entities.course.Course;
import com.edu.cringearium.repositories.course.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseRepository courseRepository;

    @Autowired
    public CourseController(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    // Получить все курсы
    @GetMapping
    public ResponseEntity<List<Course>> getAllCourses() {
        List<Course> courses = courseRepository.findAll();
        return new ResponseEntity<>(courses, HttpStatus.OK);
    }

    // Получить курс по ID
    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable Long id) {
        Optional<Course> course = courseRepository.findById(id);
        return course.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Создать новый курс
    @PostMapping
    public ResponseEntity<Course> createCourse(@RequestBody Course course) {
        // Устанавливаем текущее время создания, если оно не установлено
        if (course.getCreatedAt() == null) {
            course.setCreatedAt(LocalDateTime.now());
        }
        Course savedCourse = courseRepository.save(course);
        return new ResponseEntity<>(savedCourse, HttpStatus.CREATED);
    }

    // Обновить курс
    @PutMapping("/{id}")
    public ResponseEntity<Course> updateCourse(@PathVariable Long id, @RequestBody Course courseDetails) {
        Optional<Course> courseOptional = courseRepository.findById(id);
        if (courseOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Course existingCourse = courseOptional.get();
        existingCourse.setCourseName(courseDetails.getCourseName());
        existingCourse.setCourseTheme(courseDetails.getCourseTheme());
        existingCourse.setPrice(courseDetails.getPrice());
        // createdAt не обновляем, так как оно не должно меняться

        Course updatedCourse = courseRepository.save(existingCourse);
        return new ResponseEntity<>(updatedCourse, HttpStatus.OK);
    }

    // Удалить курс
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteCourse(@PathVariable Long id) {
        try {
            courseRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Поиск курсов по теме
    @GetMapping("/theme/{theme}")
    public ResponseEntity<List<Course>> getCoursesByTheme(@PathVariable String theme) {
        List<Course> courses = courseRepository.findByCourseThemeContainingIgnoreCase(theme);
        return new ResponseEntity<>(courses, HttpStatus.OK);
    }

    // Поиск курсов по имени
    @GetMapping("/name/{name}")
    public ResponseEntity<List<Course>> getCoursesByName(@PathVariable String name) {
        List<Course> courses = courseRepository.findByCourseNameContainingIgnoreCase(name);
        return new ResponseEntity<>(courses, HttpStatus.OK);
    }
}
