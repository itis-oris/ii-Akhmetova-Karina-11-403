package org.example.cakeshop.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
//Для создания/редактирования кондитера
public class ConfectionerRequest {

    @NotBlank
    private String name;

    private String phone;

    @NotNull(message = "Выберите точку сети")
    private Long shopId;
}
