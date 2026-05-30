package org.example.cakeshop.repository;

import org.example.cakeshop.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShopRepository extends JpaRepository<Shop, Long> {

    //список всех магазинов сорт по id в возр
    List<Shop> findAllByOrderByIdAsc();
}
