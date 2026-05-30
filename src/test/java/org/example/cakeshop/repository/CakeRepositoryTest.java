package org.example.cakeshop.repository;

import org.example.cakeshop.entity.Cake;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest //поднимает датуjpa
@Import(CakeRepositoryImpl.class)
@ActiveProfiles("test")
class CakeRepositoryTest {

    @Autowired
    CakeRepository cakeRepository;

    @BeforeEach //перед каждым тестов выполни
    void seed() {
        cakeRepository.deleteAll(); //очищаем таблицу

        Cake cheap = new Cake(); //создаем дешевый торт
        cheap.setName("Медовик");
        cheap.setSku("T-1");
        cheap.setCategory("Бисквитные");
        cheap.setPrice(1000.0);
        cheap.setNetWeightKg(1.0);

        Cake expensive = new Cake(); //дорогой торт
        expensive.setName("Премиум");
        expensive.setSku("T-2");
        expensive.setCategory("Муссовые");
        expensive.setPrice(3000.0);
        expensive.setNetWeightKg(1.5);

        cakeRepository.saveAll(List.of(cheap, expensive)); //сохраняем в h2
    }

    @Test
    void findCheaperThanCatalogAveragePrice_returnsOnlyBelowAverage() {
        List<Cake> result = cakeRepository.findCheaperThanCatalogAveragePrice(); //вызываем наш метод

        assertThat(result).hasSize(1); //проверяем что один и медовик
        assertThat(result.get(0).getName()).isEqualTo("Медовик");
    }

    @Test
    void searchByNameFragmentAndMinPrice_usesCriteriaBuilder() {
        //в названии есть мед
        List<Cake> byName = cakeRepository.searchByNameFragmentAndMinPrice("мед", null);
        //цена не ниже 2500
        List<Cake> byPrice = cakeRepository.searchByNameFragmentAndMinPrice(null, 2500.0);

        assertThat(byName).extracting(Cake::getSku).containsExactly("T-1");
        assertThat(byPrice).extracting(Cake::getSku).containsExactly("T-2");
    }
}
