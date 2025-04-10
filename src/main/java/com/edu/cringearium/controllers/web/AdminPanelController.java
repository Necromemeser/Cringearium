package com.edu.cringearium.controllers.web;

import ch.qos.logback.core.model.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminPanelController {

    @GetMapping("admin-panel")
    public String adminPanel(Model model) {return "admin-panel";}
}
