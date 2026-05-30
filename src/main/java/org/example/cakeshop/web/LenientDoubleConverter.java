package org.example.cakeshop.web;

import org.springframework.core.convert.converter.Converter;

// Преобразует строку из формы в Double
public class LenientDoubleConverter implements Converter<String, Double> {

    @Override
    public Double convert(String source) { //проверка на пустоту
        if (source == null || source.isBlank()) {
            return null;
        }
        //заменяем , на . и неразрывный пробел на обычный
        String n = source.trim().replace(',', '.').replace('\u00a0', ' ').trim();
        if (n.isEmpty()) {
            return null;
        }
        //переводим из строки в дробное
        try {
            return Double.valueOf(n);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Неверное число.");
        }
    }
}
