package org.example.cakeshop.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "confectioners")
@Getter @Setter @NoArgsConstructor
public class Confectioner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String phone;


    //кондитер не может существовать без магазина
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    //у 1 кондитера много тортов
    @ManyToMany(mappedBy = "confectioners")
    private List<Cake> cakes = new ArrayList<>();

    //у 1 кондитера много заказов
    @OneToMany(mappedBy = "confectioner")
    private List<Order> orders = new ArrayList<>();
}
