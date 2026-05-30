package org.example.cakeshop.config;

import org.example.cakeshop.web.LenientDoubleConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    //регистрируем личный конвертер чисел с запятой в точку и тд
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new LenientDoubleConverter());
    }
}
