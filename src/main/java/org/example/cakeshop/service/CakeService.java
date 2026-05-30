package org.example.cakeshop.service;

import org.example.cakeshop.dto.CakeCatalogDto;
import org.example.cakeshop.dto.CakeRequest;
import org.example.cakeshop.entity.Cake;
import org.example.cakeshop.entity.Confectioner;
import org.example.cakeshop.exception.AppException;
import org.example.cakeshop.exception.NotFoundException;
import org.example.cakeshop.repository.CakeRepository;
import org.example.cakeshop.repository.ConfectionerRepository;
import org.example.cakeshop.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CakeService {
    private final CakeRepository cakeRepository;
    private final ConfectionerRepository confectionerRepository;
    private final ReviewRepository reviewRepository;

    public CakeService(CakeRepository cakeRepository,
                       ConfectionerRepository confectionerRepository,
                       ReviewRepository reviewRepository) {
        this.cakeRepository = cakeRepository;
        this.confectionerRepository = confectionerRepository;
        this.reviewRepository = reviewRepository;
    }

    //все торты
    public List<Cake> getAll() {
        return cakeRepository.findAll();
    }

    //торты кондитера
    public List<Cake> getByConfectionerId(Long confectionerId) {
        return cakeRepository.findByConfectionerIdForCatalog(confectionerId);
    }

    //DTO для каталога кондитера
    public List<CakeCatalogDto> getCatalogDtosByConfectionerId(Long confectionerId) {
        return getByConfectionerId(confectionerId).stream().map(this::toCatalogDto).toList();
    }

    //торты дешевле среднего
    public List<Cake> findCheaperThanCatalogAveragePrice() {
        return cakeRepository.findCheaperThanCatalogAveragePrice();
    }

    //поиск с фильтрами
    public List<Cake> searchByNameFragmentAndMinPrice(String nameFragment, Double minPrice) {
        return cakeRepository.searchByNameFragmentAndMinPrice(nameFragment, minPrice);
    }

    //средняя оценка торта
    public Double averageRating(Long cakeId) {
        return reviewRepository.averageRatingForCake(cakeId);
    }

    //сущности в DTO
    public CakeCatalogDto toCatalogDto(Cake cake) {
        return CakeCatalogDto.from(cake, averageRating(cake.getId()));
    }

    public Cake getById(Long id) {
        Cake cake = cakeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Торт не найден"));
        cake.getConfectioners().size();
        return cake;
    }

    @Transactional
    public Cake create(CakeRequest request) {
        Cake cake = new Cake();
        apply(cake, request);
        return cakeRepository.save(cake);
    }

    @Transactional
    public Cake update(Long id, CakeRequest request) {
        Cake cake = getById(id);
        apply(cake, request);
        return cakeRepository.save(cake);
    }

    @Transactional
    public void delete(Long id) {
        if (!cakeRepository.existsById(id)) {
            throw new NotFoundException("Торт не найден");
        }
        cakeRepository.deleteById(id);
    }

    private void apply(Cake cake, CakeRequest request) {
        //проверка уникальности артикула
        String sku = trimToNull(request.getSku());
        if (sku == null) {
            throw new AppException("Артикул обязателен.");
        }
        boolean skuTaken = cake.getId() == null
                ? cakeRepository.existsBySku(sku)
                : cakeRepository.existsBySkuAndIdNot(sku, cake.getId());
        if (skuTaken) {
            throw new AppException("Артикул занят.");
        }

        cake.setName(request.getName().trim());
        cake.setSku(sku);
        cake.setPrice(request.getPrice());

        String category = trimToNull(request.getCategory());
        cake.setCategory(category != null ? category : "Без категории");

        Integer g = request.getNetWeightG();
        cake.setNetWeightKg(g == null ? null : (g / 1000.0));

        //кондитера по айди с магазином
        Confectioner confectioner = confectionerRepository.findByIdWithShop(request.getConfectionerId())
                .orElseThrow(() -> new NotFoundException("Кондитер не найден"));
        cake.getConfectioners().clear(); //очищаем текущ список при обновлении
        cake.getConfectioners().add(confectioner); //добавляем выбранного
    }

    private static String trimToNull(String s) {
        if (s == null) {
            return null;
        }
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}
