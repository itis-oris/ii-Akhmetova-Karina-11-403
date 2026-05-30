package org.example.cakeshop.service;

import org.example.cakeshop.dto.GeoPoint;
import org.springframework.stereotype.Service;

import java.util.Optional;

//преобразовать текстовый адрес в координаты через Яндекс.Геокодер
@Service
public class AddressLocationService {

    private final YandexGeocoderService yandexGeocoderService;

    public AddressLocationService(YandexGeocoderService yandexGeocoderService) {
        this.yandexGeocoderService = yandexGeocoderService;
    }

    public Optional<GeoPoint> geocode(String address) {
        if (address == null || address.isBlank()) {
            return Optional.empty();
        }
        GeoPoint point = yandexGeocoderService.geocode(address);
        return point == null ? Optional.empty() : Optional.of(point);
    }
}
