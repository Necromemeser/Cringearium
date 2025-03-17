package com.edu.cringearium.controllers;

import com.edu.cringearium.dto.UserDTO;
import com.edu.cringearium.entities.User;
import com.edu.cringearium.entities.UserRole;
import com.edu.cringearium.repositories.UserRepository;
import com.edu.cringearium.repositories.UserRoleRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    public UserController(UserRepository userRepository, UserRoleRepository userRoleRepository) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
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

        User user = new User(
                userDTO.getUsername(),
                userDTO.getEmail(),
                userDTO.getPasswordHash(),
                userDTO.getProfilePic(),
                userRoleOpt.get()
        );

        System.out.println("СЮДА СМОТРИ\n\n\n" + userRoleOpt.get() + "\n\n");


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
