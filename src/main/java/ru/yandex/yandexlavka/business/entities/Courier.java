package ru.yandex.yandexlavka.business.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.yandex.yandexlavka.business.DTO.CourierType;

import java.time.LocalTime;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "couriers")
public class Courier {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Enumerated(EnumType.STRING)
    private CourierType courierType;
    @ElementCollection
    private List<Integer> regions;
    @ElementCollection
    private List<LocalTime> startTimeList;
    @ElementCollection
    private List<LocalTime> endTimeList;
    @OneToMany
    @JoinColumn(name = "courier_id")
    private List<CompleteOrder> completeOrders;
//
//    @ElementCollection
//    private List<AssignedOrdersGroup> assignedOrders;
//    @ElementCollection
//    private List<Integer> curFreeWeight;
//    @ElementCollection
//    private List<Integer> curOrderNumber;
}
