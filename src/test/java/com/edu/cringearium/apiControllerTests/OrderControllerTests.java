package com.edu.cringearium.apiControllerTests;


import com.edu.cringearium.controllers.api.OrderController;
import com.edu.cringearium.dto.order.OrderDTO;
import com.edu.cringearium.dto.order.OrderResponseDTO;
import com.edu.cringearium.entities.Order;
import com.edu.cringearium.entities.course.Course;
import com.edu.cringearium.entities.user.User;
import com.edu.cringearium.repositories.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderControllerTests {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderController orderController;

    private Order testOrder;
    private OrderDTO testOrderDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        Course course = new Course();
        course.setId(1L);

        User user = new User();
        user.setId(1L);

        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setStatus("CREATED");
        testOrder.setCreatedAt(LocalDateTime.now());
        testOrder.setCourse(course);
        testOrder.setUser(user);

        testOrderDTO = new OrderDTO();
        testOrderDTO.setStatus("CREATED");
        testOrderDTO.setCourseId(1L);
        testOrderDTO.setUserId(1L);
    }

    @Test
    void getAllOrders_ReturnsListOfOrders() {
        // Arrange
        when(orderRepository.findAll()).thenReturn(Arrays.asList(testOrder));

        // Act
        ResponseEntity<List<OrderResponseDTO>> response = orderController.getAllOrders();

        // Assert
        assertEquals(1, response.getBody().size());
        assertEquals(testOrder.getId(), response.getBody().get(0).getId());
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void getOrderById_WhenOrderExists_ReturnsOrder() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        // Act
        ResponseEntity<OrderResponseDTO> response = orderController.getOrderById(1L);

        // Assert
        assertEquals(testOrder.getId(), response.getBody().getId());
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    void getOrderById_WhenOrderNotExists_ThrowsException() {
        // Arrange
        when(orderRepository.findById(2L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> orderController.getOrderById(2L));
    }

//    @Test
//    void createOrder_WithValidData_ReturnsCreatedOrder() {
//        // Arrange
//        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
//
//        // Act
//        ResponseEntity<?> response = orderController.createOrder(testOrderDTO);
//
//        // Assert
//        assertEquals(testOrder.getId(), response.getBody().getId());
//        assertEquals(testOrder.getStatus(), response.getBody().getStatus());
//        verify(orderRepository, times(1)).save(any(Order.class));
//    }

    @Test
    void updateOrder_WhenOrderExists_UpdatesAndReturnsOrder() {
        // Arrange
        OrderDTO updateDTO = new OrderDTO();
        updateDTO.setStatus("UPDATED");
        updateDTO.setCourseId(2L);
        updateDTO.setUserId(2L);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // Act
        ResponseEntity<OrderResponseDTO> response = orderController.updateOrder(1L, updateDTO);

        // Assert
        assertEquals("UPDATED", response.getBody().getStatus());
        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void updateOrderStatus_WhenOrderExists_UpdatesStatus() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // Act
        ResponseEntity<OrderResponseDTO> response =
                orderController.updateOrderStatus(1L, "UPDATED");

        // Assert
        assertEquals("UPDATED", response.getBody().getStatus());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void deleteOrder_WhenOrderExists_DeletesOrder() {
        // Arrange
        doNothing().when(orderRepository).deleteById(1L);

        // Act
        ResponseEntity<Void> response = orderController.deleteOrder(1L);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        verify(orderRepository, times(1)).deleteById(1L);
    }

    @Test
    void getOrdersByUserId_ReturnsFilteredOrders() {
        // Arrange
        User user1 = new User();
        user1.setId(1L);

        User user2 = new User();
        user2.setId(2L);

        Course course1 = new Course();
        course1.setId(10L);

        Course course2 = new Course();
        course2.setId(20L);

        Order orderForUser1 = new Order();
        orderForUser1.setId(1L);
        orderForUser1.setUser(user1);
        orderForUser1.setCourse(course1); // Добавляем курс

        Order orderForUser2 = new Order();
        orderForUser2.setId(2L);
        orderForUser2.setUser(user2);
        orderForUser2.setCourse(course2); // Добавляем курс

        when(orderRepository.findAll()).thenReturn(List.of(
                orderForUser1,
                orderForUser2
        ));

        // Act
        ResponseEntity<List<OrderResponseDTO>> response =
                orderController.getOrdersByUserId(1L);

        // Assert
        assertEquals(1, response.getBody().size());
        assertEquals(1L, response.getBody().get(0).getUserId());
        assertEquals(10L, response.getBody().get(0).getCourseId()); // Проверяем курс
    }

    @Test
    void getOrdersByCourseId_ReturnsFilteredOrders() {
        // Arrange
        // Создаем тестовых пользователей
        User user1 = new User();
        user1.setId(100L);

        User user2 = new User();
        user2.setId(200L);

        // Создаем тестовые курсы
        Course course1 = new Course();
        course1.setId(1L);

        Course course2 = new Course();
        course2.setId(2L);

        // Создаем тестовые заказы с полными данными
        Order orderForCourse1 = new Order();
        orderForCourse1.setId(1L);
        orderForCourse1.setCourse(course1);
        orderForCourse1.setUser(user1); // Добавляем пользователя
        orderForCourse1.setStatus("CREATED");
        orderForCourse1.setCreatedAt(LocalDateTime.now());

        Order orderForCourse2 = new Order();
        orderForCourse2.setId(2L);
        orderForCourse2.setCourse(course2);
        orderForCourse2.setUser(user2); // Добавляем пользователя
        orderForCourse2.setStatus("CREATED");
        orderForCourse2.setCreatedAt(LocalDateTime.now());

        // Мокируем вызов репозитория
        when(orderRepository.findAll()).thenReturn(List.of(
                orderForCourse1,
                orderForCourse2
        ));

        // Act
        ResponseEntity<List<OrderResponseDTO>> response =
                orderController.getOrdersByCourseId(1L);

        // Assert
        assertNotNull(response.getBody(), "Response body should not be null");
        assertEquals(1, response.getBody().size(), "Should return exactly 1 order for course 1");
        assertEquals(1L, response.getBody().get(0).getCourseId(), "Course ID should match");
        assertEquals(100L, response.getBody().get(0).getUserId(), "User ID should be preserved");
    }
}
