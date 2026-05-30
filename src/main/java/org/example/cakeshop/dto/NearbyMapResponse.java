package org.example.cakeshop.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NearbyMapResponse {
    private String message;
    private NearestShopDto nearestShop;
    private List<ProfileConfectionerDto> confectionersAtNearest;
    private String yandexMapUrl;
}
