package com.edu.cringearium.controllers.web;

import ch.qos.logback.core.model.Model;
import com.edu.cringearium.entities.course.Course;
import com.edu.cringearium.entities.user.User;
import com.edu.cringearium.repositories.course.CourseRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.edu.cringearium.repositories.user.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class CoursePageController {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    public CoursePageController(UserRepository userRepository, CourseRepository courseRepository) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
    }

    @GetMapping("/courses")
    public String courses(Model model) {
        return "courses";
    }

    @GetMapping("/courses/{id}")
    public String coursePage(Model model) {return "coursePage";}

    @GetMapping("/courses/{id}/study")
    public String studyPage(@AuthenticationPrincipal UserDetails userDetails,
                            @PathVariable Long id, Model model) {

        if (userDetails == null) {
            return "redirect:/login";
        }

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Курс с ID " + id + " не найден"
                ));

        if (course.getUsers().contains(user)) {
            return "study";
        }
        else {
            return "no-access";
        }
    }
}
