package org.example.cakeshop.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cakes")
@Getter @Setter @NoArgsConstructor
public class Cake {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true)
    private String sku;

    @Column(nullable = false)
    private Double price;

    @Column(length = 120)
    private String category;

    @Column(name = "net_weight_kg")
    private Double netWeightKg;

    @ManyToMany
    @JoinTable(
            name = "confectioner_cake", //таблица связи
            joinColumns = @JoinColumn(name = "cake_id"),
            inverseJoinColumns = @JoinColumn(name = "confectioner_id")
    )
    @JsonIgnore
    private List<Confectioner> confectioners = new ArrayList<>();

    @OneToMany(mappedBy = "cake", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Review> reviews = new ArrayList<>();
}
