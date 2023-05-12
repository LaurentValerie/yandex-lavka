package ru.yandex.yandexlavka.business.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    @JsonProperty("order_id")
    private Long id;
    @NotNull
    private double weight;
    @NotNull
    private int regions;
    @NotEmpty
    @JsonProperty("delivery_hours")
    private List<String> deliveryHours;
    @NotNull
    private int cost;
    @JsonProperty("completed_time")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Instant completedTime;
}
