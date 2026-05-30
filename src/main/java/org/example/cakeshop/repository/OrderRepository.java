package org.example.cakeshop.repository;

import org.example.cakeshop.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    //заказы пользователя с подробностями
    @Query("""
            SELECT DISTINCT o FROM Order o
            JOIN FETCH o.cake
            JOIN FETCH o.confectioner
            WHERE o.customer.id = :customerId
            ORDER BY o.orderDate DESC""")
    List<Order> findByCustomerIdWithDetailsOrderByOrderDateDesc(@Param("customerId") Long customerId);

    //количество заказов у кондитера
    long countByConfectionerId(Long confectionerId);

    //проверка принадлежности заказа пользователю
    @Query("SELECT o FROM Order o WHERE o.id = :orderId AND o.customer.id = :customerId")
    Optional<Order> findByIdAndCustomerId(@Param("orderId") Long orderId, @Param("customerId") Long customerId);
}
