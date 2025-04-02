package com.edu.cringearium.controllers.web;

import ch.qos.logback.core.model.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CoursePageController {

    @GetMapping("/courses")
    public String courses(Model model) {
        return "courses";
    }

    @GetMapping("/courses/{id}")
    public String coursePage(Model model) {return "coursePage";}
}
