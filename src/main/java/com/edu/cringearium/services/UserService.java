package com.edu.cringearium.services;

import com.edu.cringearium.dto.course.UserCourseDTO;
import com.edu.cringearium.entities.user.User;
import com.edu.cringearium.repositories.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public List<UserCourseDTO> getUserCourses(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        return user.getCourses().stream()
                .map(UserCourseDTO::new)
                .toList();
    }
}

