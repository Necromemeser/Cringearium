package com.edu.cringearium.controllers.api.user;

import com.edu.cringearium.config.security.CustomUserDetails;
import com.edu.cringearium.dto.course.UserCourseDTO;
import com.edu.cringearium.dto.user.UserDTO;
import com.edu.cringearium.dto.user.UserRegistrationDTO;
import com.edu.cringearium.entities.user.User;
import com.edu.cringearium.entities.user.UserRole;
import com.edu.cringearium.entities.course.Course;

import com.edu.cringearium.repositories.user.UserRepository;
import com.edu.cringearium.repositories.user.UserRoleRepository;
import com.edu.cringearium.services.UserService;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    private final UserService userService;

    public UserController(UserRepository userRepository, UserRoleRepository userRoleRepository,
                          PasswordEncoder passwordEncoder, UserService userService) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }

    @Transactional
    @GetMapping
    public List<Map<String, Object>> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> {
                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("id", user.getId());
                    userMap.put("username", user.getUsername());
                    userMap.put("email", user.getEmail());
                    userMap.put("profilePic", user.getProfilePic());

                    // Добавляем только название роли
                    if (user.getUserRole() != null) {
                        userMap.put("userRole", user.getUserRole().getRoleName());
                    }

                    // Добавляем названия курсов пользователя
                    if (user.getCourses() != null && !user.getCourses().isEmpty()) {
                        List<String> courseNames = user.getCourses().stream()
                                .map(Course::getCourseName)
                                .collect(Collectors.toList());
                        userMap.put("courses", courseNames);
                    }

                    return userMap;
                })
                .collect(Collectors.toList());
    }


    @Transactional
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userRepository.findById(id);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }


    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody UserDTO userDTO) {
        Optional<UserRole> userRoleOpt = userRoleRepository.findById(userDTO.getUserRoleId());
        if (userRoleOpt.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        String encodedPassword = passwordEncoder.encode(userDTO.getPasswordHash());

        User user = new User(
                userDTO.getUsername(),
                userDTO.getEmail(),
                encodedPassword,
                userDTO.getProfilePic(),
                userRoleOpt.get()
        );

        return ResponseEntity.ok(userRepository.save(user));
    }

    @PostMapping("/registration")
    public ResponseEntity<User> registerUser(@RequestBody UserRegistrationDTO userDTO) {
        Optional<UserRole> userRoleOpt = userRoleRepository.findById(3L);

        System.out.println(userDTO.getUsername());
        System.out.println(userDTO.getEmail());
        System.out.println(userDTO.getPasswordHash());
        String encodedPassword = passwordEncoder.encode(userDTO.getPasswordHash());

        User user = new User(
                userDTO.getUsername(),
                userDTO.getEmail(),
                encodedPassword,
                userRoleOpt.get()
        );

        return ResponseEntity.ok(userRepository.save(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable("id") Long id, @RequestBody UserDTO userDTO) {
        System.out.println(id);
        System.out.println(userDTO.getUsername());
        System.out.println(userRepository.findById(id).map(User::getUsername));
        return userRepository.findById(id)
                .map(user -> {
                    user.setUsername(userDTO.getUsername());
                    user.setEmail(userDTO.getEmail());

                    // Если пароль не обновляется, то с клиента приходит null
                    if (userDTO.getPasswordHash() != null) {
                        user.setPasswordHash(passwordEncoder.encode(userDTO.getPasswordHash()));
                    }

                    System.out.println("userRole: " + userDTO.getUserRoleId());

                    System.out.println("Before finding role");
                    Optional<UserRole> userRoleOpt = userRoleRepository.findById(userDTO.getUserRoleId());
                    System.out.println("After finding role" + userRoleOpt.get());
                    if (userRoleOpt.isEmpty()) {
                        return ResponseEntity.badRequest().build();
                    }
                    user.setUserRole(userRoleOpt.get());
                    System.out.println("Got our role");

                    userRepository.save(user);
                    System.out.println("SAVE");
                    return ResponseEntity.ok().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/ban")
    public ResponseEntity<?> banUser(@PathVariable("id") Long id) {
        return userRepository.findById(id)
                .map(user -> {
                    Optional<UserRole> userRoleOpt = userRoleRepository.findById(2L);
                    if (userRoleOpt.isEmpty()) {
                        return ResponseEntity.badRequest().build();
                    }
                    user.setUserRole(userRoleOpt.get());
                    userRepository.save(user);
                    return ResponseEntity.ok().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }


    // Для вывода курсов в личном кабинете
    @Transactional
    @GetMapping("/courses")
    public ResponseEntity<List<UserCourseDTO>> getUserCourses(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<UserCourseDTO> courses = userService.getUserCourses(userDetails.getUser().getId());
        return ResponseEntity.ok(courses);
    }




}
