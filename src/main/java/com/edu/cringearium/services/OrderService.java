package com.edu.cringearium.services;

import com.edu.cringearium.entities.Order;
import com.edu.cringearium.entities.course.Course;
import com.edu.cringearium.entities.user.User;
import com.edu.cringearium.repositories.OrderRepository;
import com.edu.cringearium.repositories.course.CourseRepository;
import com.edu.cringearium.repositories.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    public OrderService(OrderRepository orderRepository, UserRepository userRepository,
                        CourseRepository courseRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
    }

    @Transactional
    public Order confirmPayment(Long orderId) {


        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Заказ не найден"));
        order.setStatus("completed");

        // Записываем пользователя на курс
        Long userId = order.getUser().getId();
        Long courseId = order.getCourse().getId();

        System.out.println("Дошли до записи пользователя на курс");
        enrollUserInCourse(userId, courseId);


        // Обновляем статус заказа в бд
        return orderRepository.save(order);
    }

    @Transactional
    public void enrollUserInCourse(Long userId, Long courseId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Курс не найден"));

        // Инициализируем коллекцию, если она null
        if (user.getCourses() == null) {
            user.setCourses(new HashSet<>());
        }

        // Добавляем курс
        user.getCourses().add(course);
        userRepository.save(user);
    }
}
