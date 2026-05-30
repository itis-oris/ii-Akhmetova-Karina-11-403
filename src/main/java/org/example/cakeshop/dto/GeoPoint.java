package org.example.cakeshop.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

// Широта и долгота
@Getter
@AllArgsConstructor
public class GeoPoint implements Serializable {
    private static final long serialVersionUID = 1L;

    private final double latitude;
    private final double longitude;
}
