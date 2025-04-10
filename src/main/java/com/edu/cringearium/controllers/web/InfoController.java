package com.edu.cringearium.controllers.web;

import ch.qos.logback.core.model.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InfoController {

    @GetMapping("/info/about")
    public String about(Model model) {return "info/about";}

    @GetMapping("/info/contacts")
    public String contacts(Model model) {return "info/contacts";}

    @GetMapping("/info/faq")
    public String faq(Model model) {return "info/faq";}

    @GetMapping("/info/privacy")
    public String privacy(Model model) {return "info/privacy";}

    @GetMapping("/info/terms")
    public String terms(Model model) {return "info/terms";}
}
