package org.example.cakeshop.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NearestShopDto {
    private Long id;
    private String shopName;
    private String shopAddress;
    private Double distanceKm;
}
