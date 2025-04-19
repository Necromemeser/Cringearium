package com.edu.cringearium.controllers.web.auth;

import org.springframework.ui.Model;
import com.edu.cringearium.dto.user.UserRegistrationDTO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.context.request.WebRequest;


@Controller
public class RegistrationController {

    @GetMapping("/registration")
    public String showRegistrationForm(WebRequest request, Model model) {
        UserRegistrationDTO userDto = new UserRegistrationDTO();
        model.addAttribute("user", userDto);
        return "auth/registration";
    }
}
