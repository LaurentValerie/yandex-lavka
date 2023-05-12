package ru.yandex.yandexlavka.presentation.models;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.yandex.yandexlavka.business.dtos.CourierDTO;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateCourierRequest {
    @NotEmpty
    private List<@Valid CourierDTO> couriers;
}
