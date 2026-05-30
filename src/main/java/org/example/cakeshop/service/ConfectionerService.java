package org.example.cakeshop.service;

import org.example.cakeshop.dto.ConfectionerRequest;
import org.example.cakeshop.entity.Cake;
import org.example.cakeshop.entity.Confectioner;
import org.example.cakeshop.entity.Shop;
import org.example.cakeshop.exception.NotFoundException;
import org.example.cakeshop.repository.CakeRepository;
import org.example.cakeshop.repository.ConfectionerRepository;
import org.example.cakeshop.repository.OrderRepository;
import org.example.cakeshop.repository.ShopRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ConfectionerService {
    private final ConfectionerRepository confectionerRepository;
    private final CakeRepository cakeRepository;
    private final OrderRepository orderRepository;
    private final ShopRepository shopRepository;

    public ConfectionerService(ConfectionerRepository confectionerRepository,
                               CakeRepository cakeRepository,
                               OrderRepository orderRepository,
                               ShopRepository shopRepository) {
        this.confectionerRepository = confectionerRepository;
        this.cakeRepository = cakeRepository;
        this.orderRepository = orderRepository;
        this.shopRepository = shopRepository;
    }

    public List<Shop> getAllShops() {
        return shopRepository.findAllByOrderByIdAsc();
    }

    public List<Confectioner> getAll() {
        return confectionerRepository.findAllWithShopOrderByNameAsc();
    }

    public Confectioner getById(Long id) {
        return confectionerRepository.findByIdWithShop(id)
                .orElseThrow(() -> new NotFoundException("Кондитер не найден"));
    }

    @Transactional
    public Confectioner create(ConfectionerRequest request) {
        Confectioner c = new Confectioner();
        apply(c, request);
        return confectionerRepository.save(c);
    }

    @Transactional
    public Confectioner update(Long id, ConfectionerRequest request) {
        Confectioner c = getById(id);
        apply(c, request);
        return confectionerRepository.save(c);
    }

    @Transactional
    public void delete(Long id) {
        if (orderRepository.countByConfectionerId(id) > 0) {
            throw new IllegalStateException("Есть заказы этого кондитера.");
        }
        Confectioner c = getById(id);
        List<Cake> linked = cakeRepository.findByConfectioners_Id(id);
        for (Cake cake : linked) {
            cake.getConfectioners().remove(c);
        }
        cakeRepository.saveAll(linked);
        confectionerRepository.delete(c);
    }

    private void apply(Confectioner target, ConfectionerRequest src) {
        target.setName(src.getName() != null ? src.getName().trim() : "");
        target.setPhone(trimToNull(src.getPhone()));
        Shop shop = shopRepository.findById(src.getShopId())
                .orElseThrow(() -> new NotFoundException("Точка сети не найдена"));
        target.setShop(shop);
    }

    private static String trimToNull(String s) {
        if (s == null) {
            return null;
        }
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}
