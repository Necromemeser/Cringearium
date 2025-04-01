package com.edu.cringearium.controllers.api.course;

import com.edu.cringearium.dto.CourseDataTextDTO;
import com.edu.cringearium.entities.course.Course;
import com.edu.cringearium.entities.course.CourseData;
import com.edu.cringearium.repositories.course.CourseDataRepository;
import com.edu.cringearium.repositories.course.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/courses/{courseId}/data")
public class CourseDataController {

    private final CourseDataRepository courseDataRepository;
    private final CourseRepository courseRepository;

    @Autowired
    public CourseDataController(CourseDataRepository courseDataRepository,
                                CourseRepository courseRepository) {
        this.courseDataRepository = courseDataRepository;
        this.courseRepository = courseRepository;
    }

    // === Текстовые операции ===

    // Добавление текстовых данных через JSON
    @PostMapping(path = "/text", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CourseData> addTextCourseData(
            @PathVariable Long courseId,
            @RequestBody CourseDataTextDTO textDTO) {

        Optional<Course> course = courseRepository.findById(courseId);
        if (course.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        CourseData courseData = new CourseData();
        courseData.setCourse(course.get());
        courseData.setContent(textDTO.getContent().getBytes());

        CourseData savedData = courseDataRepository.save(courseData);
        return new ResponseEntity<>(savedData, HttpStatus.CREATED);
    }

    // Обновление текстовых данных через JSON
    @PutMapping(path = "/text/{dataId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CourseData> updateTextCourseData(
            @PathVariable Long courseId,
            @PathVariable Long dataId,
            @RequestBody CourseDataTextDTO textDTO) {

        Optional<CourseData> existingData = courseDataRepository.findById(dataId);
        if (existingData.isEmpty() ||
                !existingData.get().getCourse().getId().equals(courseId)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        CourseData courseData = existingData.get();
        courseData.setContent(textDTO.getContent().getBytes());

        CourseData updatedData = courseDataRepository.save(courseData);
        return new ResponseEntity<>(updatedData, HttpStatus.OK);
    }

    // === Файловые операции ===

    // Загрузка файла
    @PostMapping(path = "/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CourseData> addFileCourseData(
            @PathVariable Long courseId,
            @RequestParam("file") MultipartFile file) throws IOException {

        Optional<Course> course = courseRepository.findById(courseId);
        if (course.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        CourseData courseData = new CourseData();
        courseData.setCourse(course.get());
        courseData.setContent(file.getBytes());

        CourseData savedData = courseDataRepository.save(courseData);
        return new ResponseEntity<>(savedData, HttpStatus.CREATED);
    }

    // Обновление файла
    @PutMapping(path = "/file/{dataId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CourseData> updateFileCourseData(
            @PathVariable Long courseId,
            @PathVariable Long dataId,
            @RequestParam("file") MultipartFile file) throws IOException {

        Optional<CourseData> existingData = courseDataRepository.findById(dataId);
        if (existingData.isEmpty() ||
                !existingData.get().getCourse().getId().equals(courseId)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        CourseData courseData = existingData.get();
        courseData.setContent(file.getBytes());

        CourseData updatedData = courseDataRepository.save(courseData);
        return new ResponseEntity<>(updatedData, HttpStatus.OK);
    }

    // === Общие операции ===

    // Получение всех данных курса
    @GetMapping
    public ResponseEntity<List<CourseData>> getAllCourseData(@PathVariable Long courseId) {
        if (!courseRepository.existsById(courseId)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<CourseData> courseData = courseDataRepository.findByCourseId(courseId);
        return new ResponseEntity<>(courseData, HttpStatus.OK);
    }

    // Получение конкретных данных по ID
    @GetMapping("/{dataId}")
    public ResponseEntity<CourseData> getCourseDataById(
            @PathVariable Long courseId,
            @PathVariable Long dataId) {

        Optional<CourseData> courseData = courseDataRepository.findById(dataId);
        if (courseData.isEmpty() || !courseData.get().getCourse().getId().equals(courseId)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(courseData.get(), HttpStatus.OK);
    }

    // Удаление данных
    @DeleteMapping("/{dataId}")
    public ResponseEntity<HttpStatus> deleteCourseData(
            @PathVariable Long courseId,
            @PathVariable Long dataId) {

        Optional<CourseData> courseData = courseDataRepository.findById(dataId);
        if (courseData.isEmpty() ||
                !courseData.get().getCourse().getId().equals(courseId)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        courseDataRepository.deleteById(dataId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}