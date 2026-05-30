package org.example.cakeshop.repository;

import org.example.cakeshop.entity.Cake;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CakeRepository extends JpaRepository<Cake, Long>, CakeRepositoryCustom {

    //существует ли торт с таким артикулом
    //при создании нового торта
    boolean existsBySku(String sku);

    //при редактировании игнорирует текущий торт
    boolean existsBySkuAndIdNot(String sku, Long id);

    // Все торты, связанные с кондитером для снятия связи при удалении мастера
    List<Cake> findByConfectioners_Id(Long confectionerId);

    //кастомный JPQL-запрос
    //выбрать торты связанные с опред кондитером для отображения в каталоге с сортировкой и без дубликатов
    @Query("""
            SELECT DISTINCT c FROM Cake c
            JOIN c.confectioners k
            WHERE k.id = :confectionerId
            ORDER BY c.name""")
    List<Cake> findByConfectionerIdForCatalog(@Param("confectionerId") Long confectionerId);

    //список всех тортов цена которых меньше средней цены всех тортов в каталоге
    @Query("SELECT c FROM Cake c WHERE c.price < (SELECT AVG(c2.price) FROM Cake c2)")
    List<Cake> findCheaperThanCatalogAveragePrice();
}
