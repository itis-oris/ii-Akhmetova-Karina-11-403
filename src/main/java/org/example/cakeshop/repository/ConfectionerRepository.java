package org.example.cakeshop.repository;

import org.example.cakeshop.entity.Confectioner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConfectionerRepository extends JpaRepository<Confectioner, Long> {

    //найти кондитера по ID с загрузкой магазина
    @Query("SELECT c FROM Confectioner c JOIN FETCH c.shop WHERE c.id = :id")
    Optional<Confectioner> findByIdWithShop(@Param("id") Long id);

    //все кондитеры с магазинами отсортированные по имени
    @Query("SELECT c FROM Confectioner c JOIN FETCH c.shop ORDER BY c.name")
    List<Confectioner> findAllWithShopOrderByNameAsc();

    //все кондитеры, отсортированные по ID
    List<Confectioner> findAllByOrderByIdAsc();

    //найти кондитеров по ID магазина
    List<Confectioner> findByShop_IdOrderByNameAsc(Long shopId);
}
