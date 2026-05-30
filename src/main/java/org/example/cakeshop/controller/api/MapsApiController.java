package org.example.cakeshop.controller.api;

import org.example.cakeshop.dto.NearbyMapResponse;
import org.example.cakeshop.entity.Customer;
import org.example.cakeshop.exception.NotFoundException;
import org.example.cakeshop.service.CustomerService;
import org.example.cakeshop.service.NearbyMapService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/maps")
public class MapsApiController {

    private final NearbyMapService nearbyMapService;
    private final CustomerService customerService;

    public MapsApiController(NearbyMapService nearbyMapService, CustomerService customerService) {
        this.nearbyMapService = nearbyMapService;
        this.customerService = customerService;
    }

    // Карта: 3 кондитерские и расстояние от адреса юзера
    @GetMapping("/nearby")
    public NearbyMapResponse nearby(Authentication authentication) {
        Customer customer = requireCustomer(authentication); //получаем юзера
        return nearbyMapService.buildForCustomer(customer.getId()); //передаем юзера в ответ о ближ магазинах
    }

    private Customer requireCustomer(Authentication authentication) {
        if (authentication == null) { //аунтефецирован ли юзер
            throw new NotFoundException("Требуется вход");
        }
        Customer customer = customerService.findByEmail(authentication.getName()); //извлекаем email
        if (customer == null) {
            throw new NotFoundException("Пользователь не найден");
        }
        return customer;
    }
}
