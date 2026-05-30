package org.example.cakeshop.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
//для создания/обновления торта
public class CakeRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String sku;

    @NotNull
    @Positive
    private Double price;

    @NotBlank
    private String category;

    @NotNull
    @Positive
    private Integer netWeightG;

    @NotNull
    private Long confectionerId;
}
