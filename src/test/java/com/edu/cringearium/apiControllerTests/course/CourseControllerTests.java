package com.edu.cringearium.apiControllerTests.course;

import com.edu.cringearium.controllers.api.course.CourseController;
import com.edu.cringearium.entities.course.Course;
import com.edu.cringearium.repositories.course.CourseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CourseControllerTests {

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private CourseController courseController;

    private Course testCourse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testCourse = new Course();
        testCourse.setId(1L);
        testCourse.setCourseName("Java Basics");
        testCourse.setCourseTheme("Programming");
        testCourse.setPrice(1000L);
        testCourse.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void getCourseById_WhenCourseExists_ReturnsCourse() {
        // Arrange
        when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));

        // Act
        ResponseEntity<Course> response = courseController.getCourseById(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testCourse, response.getBody());
        verify(courseRepository, times(1)).findById(1L);
    }

    @Test
    void getCourseById_WhenCourseNotExists_ReturnsNotFound() {
        // Arrange
        when(courseRepository.findById(2L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Course> response = courseController.getCourseById(2L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(courseRepository, times(1)).findById(2L);
    }

    @Test
    void deleteCourse_WhenCourseExists_ReturnsNoContent() {
        // Arrange
        doNothing().when(courseRepository).deleteById(1L);

        // Act
        ResponseEntity<HttpStatus> response = courseController.deleteCourse(1L);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(courseRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteCourse_WhenErrorOccurs_ReturnsInternalServerError() {
        // Arrange
        doThrow(new RuntimeException()).when(courseRepository).deleteById(1L);

        // Act
        ResponseEntity<HttpStatus> response = courseController.deleteCourse(1L);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(courseRepository, times(1)).deleteById(1L);
    }

    @Test
    void getCoursesByTheme_ReturnsMatchingCourses() {
        // Arrange
        when(courseRepository.findByCourseThemeContainingIgnoreCase("prog"))
                .thenReturn(Arrays.asList(testCourse));

        // Act
        ResponseEntity<List<Course>> response = courseController.getCoursesByTheme("prog");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(testCourse, response.getBody().get(0));
        verify(courseRepository, times(1)).findByCourseThemeContainingIgnoreCase("prog");
    }

    @Test
    void getCoursesByTheme_WhenNoMatches_ReturnsEmptyList() {
        // Arrange
        when(courseRepository.findByCourseThemeContainingIgnoreCase("math"))
                .thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<List<Course>> response = courseController.getCoursesByTheme("math");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(courseRepository, times(1)).findByCourseThemeContainingIgnoreCase("math");
    }

    @Test
    void getCoursesByName_ReturnsMatchingCourses() {
        // Arrange
        when(courseRepository.findByCourseNameContainingIgnoreCase("java"))
                .thenReturn(Arrays.asList(testCourse));

        // Act
        ResponseEntity<List<Course>> response = courseController.getCoursesByName("java");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(testCourse, response.getBody().get(0));
        verify(courseRepository, times(1)).findByCourseNameContainingIgnoreCase("java");
    }

    @Test
    void getCoursesByName_WhenNoMatches_ReturnsEmptyList() {
        // Arrange
        when(courseRepository.findByCourseNameContainingIgnoreCase("python"))
                .thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<List<Course>> response = courseController.getCoursesByName("python");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(courseRepository, times(1)).findByCourseNameContainingIgnoreCase("python");
    }
}