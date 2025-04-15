package com.edu.cringearium.controllers.api.course;

import com.edu.cringearium.dto.course.CoursePageDTO;
import com.edu.cringearium.entities.course.Course;
import com.edu.cringearium.repositories.course.CourseRepository;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseRepository courseRepository;

    @Autowired
    public CourseController(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Transactional
    @GetMapping
    public ResponseEntity<List<CoursePageDTO>> getAllCourses(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String theme
    ) {
        List<Course> courses;

        if (name != null && theme != null) {
            courses = courseRepository.findByCourseNameContainingIgnoreCaseAndCourseThemeIgnoreCase(name, theme);
        } else if (name != null) {
            courses = courseRepository.findByCourseNameContainingIgnoreCase(name);
        } else if (theme != null) {
            courses = courseRepository.findByCourseThemeContainingIgnoreCase(theme);
        } else {
            courses = courseRepository.findAll();
        }

        List<CoursePageDTO> result = courses.stream()
                .map(CoursePageDTO::new)
                .toList();

        return ResponseEntity.ok(result);
    }
    @GetMapping("/admin")
    public List<Map<String, Object>> getAllCoursesForAdmin() {
        return courseRepository.findAll().stream()
                .map(course -> {
                    Map<String, Object> courseMap = new HashMap<>();
                    courseMap.put("id", course.getId());
                    courseMap.put("courseName", course.getCourseName());
                    courseMap.put("courseTheme", course.getCourseTheme());
                    courseMap.put("createdAt", course.getCreatedAt());
                    courseMap.put("price", course.getPrice());
                    courseMap.put("courseDescription", course.getCourseDescription());
                    courseMap.put("courseImage", course.getCourseImage());

                    return courseMap;
                })
                .collect(Collectors.toList());
    }

    // Получить курс по ID
    @Transactional
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
        existingCourse.setCourseDescription(courseDetails.getCourseDescription());

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

    // Загрузить изображение курса
    @PostMapping("/{id}/upload-image")
    public ResponseEntity<String> uploadCourseImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        Optional<Course> courseOptional = courseRepository.findById(id);
        if (courseOptional.isEmpty()) {
            return new ResponseEntity<>("Курс не найден", HttpStatus.NOT_FOUND);
        }

        try {
            Course course = courseOptional.get();
            course.setCourseImage(file.getBytes());
            courseRepository.save(course);
            return new ResponseEntity<>("Изображение загружено", HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>("Ошибка загрузки файла", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Получить изображение курса
    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getCourseImage(@PathVariable Long id) {
        Optional<Course> courseOptional = courseRepository.findById(id);
        if (courseOptional.isEmpty() || courseOptional.get().getCourseImage() == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Course course = courseOptional.get();
        byte[] imageBytes = course.getCourseImage();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG); // Можно заменить на IMAGE_PNG, если загружаются PNG-файлы

        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
    }

    // Получить изображение курса в Base64
    @GetMapping("/{id}/image-base64")
    public ResponseEntity<String> getCourseImageBase64(@PathVariable Long id) {
        Optional<Course> courseOptional = courseRepository.findById(id);
        if (courseOptional.isEmpty() || courseOptional.get().getCourseImage() == null) {
            return new ResponseEntity<>("Изображение не найдено", HttpStatus.NOT_FOUND);
        }

        Course course = courseOptional.get();
        String base64Image = Base64.getEncoder().encodeToString(course.getCourseImage());
        return new ResponseEntity<>(base64Image, HttpStatus.OK);
    }

}
