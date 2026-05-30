package org.example.cakeshop.repository;

import org.example.cakeshop.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    //все отзывы на те торты
    //которые готовил кондитер с указанным Id
    //и подгружаются покупатели
    @Query("""
            SELECT DISTINCT r FROM Review r
            JOIN FETCH r.customer
            JOIN FETCH r.cake cake
            JOIN cake.confectioners k
            WHERE k.id = :confectionerId
            ORDER BY r.createdAt DESC""")
    List<Review> findRecentForConfectionerCatalog(@Param("confectionerId") Long confectionerId);

    //средний рейтинг торта
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.cake.id = :cakeId")
    Double averageRatingForCake(@Param("cakeId") Long cakeId);
}
