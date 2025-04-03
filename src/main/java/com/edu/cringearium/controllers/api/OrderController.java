package com.edu.cringearium.controllers.api;

import com.edu.cringearium.dto.order.OrderDTO;
import com.edu.cringearium.dto.order.OrderResponseDTO;
import com.edu.cringearium.entities.Order;
import com.edu.cringearium.entities.course.Course;
import com.edu.cringearium.entities.user.User;
import com.edu.cringearium.repositories.OrderRepository;
import com.edu.cringearium.repositories.user.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public OrderController(OrderRepository orderRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    // Получить все заказы
    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders() {
        List<OrderResponseDTO> orders = orderRepository.findAll().stream()
                .map(OrderResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(orders);
    }

    // Получить заказ по ID
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
        return ResponseEntity.ok(new OrderResponseDTO(order));
    }

    // Получить заказы пользователя
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponseDTO>> getOrdersByUserId(@PathVariable Long userId) {
        List<OrderResponseDTO> userOrders = orderRepository.findAll().stream()
                .filter(order -> order.getUser().getId().equals(userId))
                .map(OrderResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userOrders);
    }

    // Создать новый заказ
    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(@RequestBody OrderDTO dto) {
        Order order = new Order();
        order.setStatus(dto.getStatus());

        Course course = new Course();
        course.setId(dto.getCourseId());
        order.setCourse(course);

        User user = new User();

        if (dto.getUserId() == null) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            // Получаем имя пользователя из аутентификации
            String username;
            if (auth.getPrincipal() instanceof UserDetails) {
                username = ((UserDetails) auth.getPrincipal()).getUsername();
            } else {
                username = auth.getPrincipal().toString();
            }

            // Находим пользователя в базе
            User currentUser = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Current user not found"));

            user.setId(currentUser.getId());
        }
        else
        {
            user.setId(dto.getUserId());
        }

        order.setUser(user);

        Order savedOrder = orderRepository.save(order);
        return ResponseEntity.ok(new OrderResponseDTO(savedOrder));
    }

    // Обновить заказ
    @PutMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> updateOrder(@PathVariable Long id, @RequestBody OrderDTO dto) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));

        existingOrder.setStatus(dto.getStatus());

        Order updatedOrder = orderRepository.save(existingOrder);
        return ResponseEntity.ok(new OrderResponseDTO(updatedOrder));
    }

    // Удалить заказ
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // Обновить статус заказа
    @PutMapping("/{id}/status")
    public ResponseEntity<OrderResponseDTO> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);
        return ResponseEntity.ok(new OrderResponseDTO(updatedOrder));
    }

    // Получить заказы по курсу
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<OrderResponseDTO>> getOrdersByCourseId(@PathVariable Long courseId) {
        List<OrderResponseDTO> courseOrders = orderRepository.findAll().stream()
                .filter(order -> order.getCourse().getId().equals(courseId))
                .map(OrderResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(courseOrders);
    }
}
