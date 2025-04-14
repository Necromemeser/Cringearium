package com.edu.cringearium.controllers.api;

import com.edu.cringearium.dto.order.OrderDTO;
import com.edu.cringearium.dto.order.OrderResponseDTO;
import com.edu.cringearium.entities.Order;
import com.edu.cringearium.entities.course.Course;
import com.edu.cringearium.entities.user.User;
import com.edu.cringearium.repositories.OrderRepository;
import com.edu.cringearium.repositories.course.CourseRepository;
import com.edu.cringearium.repositories.user.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    private final CourseRepository courseRepository;

    @Value("${SHOP_ID}")
    private String shopId;

    @Value("${SHOP_API_KEY}")
    private String secretKey;

    public OrderController(OrderRepository orderRepository, UserRepository userRepository, CourseRepository courseRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
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
    public ResponseEntity<?> createOrder(@RequestBody OrderDTO dto) {
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

        Long amount = courseRepository.findPriceById(dto.getCourseId());
        if (amount == null || amount <= 0) {
            throw new RuntimeException("У курса не установлена корректная цена");
        }

        String courseName = courseRepository.findCourseNameById(dto.getCourseId());


        String paymentUrl = createYooKassaPayment(savedOrder.getId(), amount, "Оплата курса " + courseName);

        return ResponseEntity.ok(Map.of(
                "orderId", savedOrder.getId(),
                "paymentUrl", paymentUrl
        ));
    }

    private String createYooKassaPayment(Long orderId, Long amount, String description) {
        try {
            // Настройка аутентификации
            String auth = shopId + ":" + secretKey;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());

            // уникальный ключ идемпотентности
            String idempotenceKey = UUID.randomUUID().toString();

            // Настройка запроса
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Basic " + encodedAuth);
            headers.set("Idempotence-Key", idempotenceKey);
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));


            String amountForYooKassa = amount + ".00";



            // Подготовка тела запроса
            Map<String, Object> request = new HashMap<>();
            request.put("amount", Map.of(
                    "value", amountForYooKassa,
                    "currency", "RUB"
            ));
            request.put("description", description);
            request.put("confirmation", Map.of(
                    "type", "redirect",
                    "return_url", "https://cringearium.loca.lt/profile" // + orderId
            ));
            request.put("capture", true);
            request.put("metadata", Map.of(
                    "orderId", orderId
            ));

            // Отправка запроса
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);
            ResponseEntity<Map> response = restTemplate.exchange(
                    "https://api.yookassa.ru/v3/payments",
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            // Обработка ответа
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map confirmation = (Map) response.getBody().get("confirmation");
                return (String) confirmation.get("confirmation_url");
            } else {
                throw new RuntimeException("Ошибка при создании платежа: " + response.getStatusCode());
            }

        } catch (Exception e) {
            throw new RuntimeException("Ошибка при работе с ЮКассой: " + e.getMessage(), e);
        }
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
