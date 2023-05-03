package ru.yandex.yandexlavka.business.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "complete_orders")
public class CompleteOrder {
    @Id
    @NotNull
    @Column(name = "order_id")
    @JsonProperty("order_id")
    private Long orderID;
    @NotNull
    @Column(name = "courier_id")
    @JsonProperty("courier_id")
    private Long courierID;
    @NotNull
    @JsonProperty("complete_time")
    private Instant completeTime;
}
