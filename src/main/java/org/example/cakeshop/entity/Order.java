package org.example.cakeshop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Getter @Setter @NoArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "confectioner_id", nullable = false)
    private Confectioner confectioner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cake_id", nullable = false)
    private Cake cake;

    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate = LocalDateTime.now();

    @Column(nullable = false)
    private String status = "Новый";

    @Column(name = "total_price", nullable = false)
    private Double totalPrice;
}
