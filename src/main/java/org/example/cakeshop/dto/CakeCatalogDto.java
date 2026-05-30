package org.example.cakeshop.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.cakeshop.entity.Cake;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CakeCatalogDto {
    private Long id;
    private String name;
    private String sku;
    private Double price;
    private String category;
    private Integer netWeightG;
    private Double averageRating;

    public static CakeCatalogDto from(Cake cake, Double averageRating) {
        Integer g = null;
        if (cake.getNetWeightKg() != null) {
            //переводим в граммы
            g = (int) Math.round(cake.getNetWeightKg() * 1000.0);
        }
        return new CakeCatalogDto(
                cake.getId(),
                cake.getName(),
                cake.getSku(),
                cake.getPrice(),
                cake.getCategory(),
                g,
                averageRating
        );
    }
}
