package ru.yandex.yandexlavka.business.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private float weight;
    private int regions;
    @ElementCollection
    private List<LocalTime> deliveryStart;
    @ElementCollection
    private List<LocalTime> deliveryEnd;
    private int cost;
    private Instant completedTime;
    private long assignedCourier;
}
