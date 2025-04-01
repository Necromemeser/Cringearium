package com.edu.cringearium.controllers.api.user;

import com.edu.cringearium.dto.UserDTO;
import com.edu.cringearium.entities.user.User;
import com.edu.cringearium.entities.user.UserRole;
import com.edu.cringearium.repositories.user.UserRepository;
import com.edu.cringearium.repositories.user.UserRoleRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepository, UserRoleRepository userRoleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

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

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setUsername(userDTO.getUsername());
                    user.setEmail(userDTO.getEmail());
                    user.setPasswordHash(userDTO.getPasswordHash());
                    user.setProfilePic(userDTO.getProfilePic());

                    Optional<UserRole> userRoleOpt = userRoleRepository.findById(userDTO.getUserRoleId());
                    if (userRoleOpt.isEmpty()) {
                        return ResponseEntity.badRequest().body(user); // Возвращаем существующего пользователя
                    }
                    user.setUserRole(userRoleOpt.get());

                    return ResponseEntity.ok(userRepository.save(user));
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
}
