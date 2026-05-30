package org.example.cakeshop.controller.api;

import jakarta.validation.Valid;
import org.example.cakeshop.dto.CakeCatalogDto;
import org.example.cakeshop.dto.CakeRequest;
import org.example.cakeshop.entity.Cake;
import org.example.cakeshop.service.CakeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
//Он работает с JSON-запросами и ответами, для админ-панели
@RestController
@RequestMapping("/api/cakes")
public class CakeApiController {
    private final CakeService cakeService;

    public CakeApiController(CakeService cakeService) {
        this.cakeService = cakeService;
    }

    //получить список тортов
    @GetMapping
    public List<CakeCatalogDto> all(@RequestParam(required = false) Long confectionerId) { //необяз параметр
        //определенного кондитера
        List<Cake> cakes = confectionerId != null
                ? cakeService.getByConfectionerId(confectionerId)
                : cakeService.getAll();
        //все торты
        return cakes.stream().map(cakeService::toCatalogDto).toList();
    }

    // JPQL с подзапросом торт цена ниже средней цены по каталогу
    @GetMapping("/cheaper-than-catalog-average")
    public List<CakeCatalogDto> cheaperThanAverage() {
        return cakeService.findCheaperThanCatalogAveragePrice().stream()
                .map(cakeService::toCatalogDto)
                .toList();
    }

    //поиск по фрагменту и минимальной цене
    @GetMapping("/search")
    public List<CakeCatalogDto> search(@RequestParam(required = false) String q,
                                       @RequestParam(required = false) Double minPrice) {
        return cakeService.searchByNameFragmentAndMinPrice(q, minPrice).stream()
                .map(cakeService::toCatalogDto)
                .toList();
    }

    //получение одного торта по ID
    @GetMapping("/{id}")
    public CakeCatalogDto byId(@PathVariable Long id) {
        return cakeService.toCatalogDto(cakeService.getById(id));
    }

    //создание нового торта
    @PostMapping
    public CakeCatalogDto create(@Valid @RequestBody CakeRequest request) {
        return cakeService.toCatalogDto(cakeService.create(request));
    }

    //обновление торта
    @PutMapping("/{id}")
    public CakeCatalogDto update(@PathVariable Long id, @Valid @RequestBody CakeRequest request) {
        return cakeService.toCatalogDto(cakeService.update(id, request));
    }

    //удаление торта
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        cakeService.delete(id);
    }
}
