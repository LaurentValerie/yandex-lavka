package ru.yandex.yandexlavka.business.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum CourierType {
    FOOT, BIKE, AUTO
}
