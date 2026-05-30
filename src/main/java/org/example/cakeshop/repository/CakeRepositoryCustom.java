package org.example.cakeshop.repository;

import org.example.cakeshop.entity.Cake;

import java.util.List;

public interface CakeRepositoryCustom {

    //поиск по фрагменту названия и минимальной цене(необяз критерии)
    List<Cake> searchByNameFragmentAndMinPrice(String nameFragment, Double minPrice);
}
