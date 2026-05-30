package org.example.cakeshop.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.cakeshop.config.YandexMapsProperties;
import org.example.cakeshop.dto.GeoPoint;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

//вызывает API Яндекс.Геокодера
@Service
public class YandexGeocoderService {

    private final HttpClient httpClient = HttpClient.newHttpClient();//клиент для отправки запросов
    private final ObjectMapper objectMapper = new ObjectMapper(); //для чтения json
    private final YandexMapsProperties properties; //бин с apikey

    public YandexGeocoderService(YandexMapsProperties properties) {
        this.properties = properties;
    }

    //кэшируем только успешные ответы
    @Cacheable(cacheNames = "yandexGeocode", key = "#address == null ? '' : #address.trim()", unless = "#result == null")
    public GeoPoint geocode(String address) {
        if (address == null || address.isBlank()) {
            return null;
        }
        //формируем запрос
        String url = "https://geocode-maps.yandex.ru/1.x/?apikey="
                + URLEncoder.encode(properties.getApiKey(), StandardCharsets.UTF_8)
                + "&geocode=" + URLEncoder.encode(address.trim(), StandardCharsets.UTF_8)
                + "&format=json&lang=ru_RU&results=1";

        try {
            //отправляем get запрос
            HttpResponse<String> response = httpClient.send(
                    HttpRequest.newBuilder().uri(URI.create(url)).GET().build(),
                    HttpResponse.BodyHandlers.ofString());
            //если не ок
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                return null;
            }
            //парсим json
            JsonNode members = objectMapper.readTree(response.body())
                    .path("response").path("GeoObjectCollection").path("featureMember");
            if (!members.isArray() || members.isEmpty()) {
                return null;
            }
            //получаем из первого значения массива долгота широта
            String pos = members.get(0).path("GeoObject").path("Point").path("pos").asText("");
            //если не два значения
            String[] parts = pos.trim().split("\\s+");
            if (parts.length < 2) {
                return null;
            }
            //возвр широта долгота наоборот
            return new GeoPoint(Double.parseDouble(parts[1]), Double.parseDouble(parts[0]));
        } catch (Exception e) {
            return null;
        }
    }
}
