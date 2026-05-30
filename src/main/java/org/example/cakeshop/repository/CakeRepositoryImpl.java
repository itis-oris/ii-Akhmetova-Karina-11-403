package org.example.cakeshop.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.example.cakeshop.entity.Cake;
import java.util.ArrayList;
import java.util.List;

// Criteria API: поиск по фрагменту в названии, категории или SKU и фильтр по минимальной цене
public class CakeRepositoryImpl implements CakeRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager; //интерфейс для работы с бд внедряем

    @Override
    public List<Cake> searchByNameFragmentAndMinPrice(String nameFragment, Double minPrice) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder(); //строитель запроса
        CriteriaQuery<Cake> cq = cb.createQuery(Cake.class); //сам запрос возвращает сущности торта
        Root<Cake> root = cq.from(Cake.class); //элемент запроса соответствующий табл cake

        List<Predicate> predicates = new ArrayList<>(); //список условий

        //содержится ли фрагмент в названии категории или артикуле
        if (nameFragment != null && !nameFragment.isBlank()) {
            String pattern = "%" + nameFragment.toLowerCase().trim() + "%";
            var namePred = cb.like(cb.lower(root.get("name")), pattern);
            var catPred = cb.like(cb.lower(root.get("category")), pattern);
            var skuPred = cb.like(cb.lower(root.get("sku")), pattern);
            predicates.add(cb.or(namePred, catPred, skuPred));
        }
        //фильтр по мин цене
        if (minPrice != null) {
            predicates.add(cb.ge(root.get("price"), minPrice));
        }

        //если нет условий
        if (predicates.isEmpty()) {
            cq.where(cb.conjunction());
        } else {
            //если есть то через энд
            cq.where(cb.and(predicates.toArray(Predicate[]::new)));
        }
        //сортировка по имени
        cq.orderBy(cb.asc(root.get("name")));

        //создается запрос и возвращается список тортов
        return entityManager.createQuery(cq).getResultList();
    }
}
