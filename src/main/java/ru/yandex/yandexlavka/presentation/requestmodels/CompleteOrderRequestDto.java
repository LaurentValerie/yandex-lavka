package ru.yandex.yandexlavka.presentation.requestmodels;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.yandex.yandexlavka.business.entities.CompleteOrder;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompleteOrderRequestDto {
    @JsonProperty("complete_info")
    private List<@Valid CompleteOrder> completeOrders;
}
