package com.edu.cringearium.repositories;

import com.edu.cringearium.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
