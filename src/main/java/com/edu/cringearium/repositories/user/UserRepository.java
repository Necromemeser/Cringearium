package com.edu.cringearium.repositories.user;


import com.edu.cringearium.entities.user.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    @EntityGraph(attributePaths = {"courses"})
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.courses WHERE u.id = :userId")
    Optional<User> findWithCoursesById(@Param("userId") Long userId);

}

