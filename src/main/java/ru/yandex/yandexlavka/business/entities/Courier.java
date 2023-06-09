package ru.yandex.yandexlavka.business.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.yandex.yandexlavka.business.dtos.CourierType;

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
    @ElementCollection(fetch = FetchType.EAGER)
    private List<Integer> regions;
    @ElementCollection(fetch = FetchType.EAGER)
    private List<LocalTime> startTimeList;
    @ElementCollection(fetch = FetchType.EAGER)
    private List<LocalTime> endTimeList;
    @OneToMany
    @JoinColumn(name = "courier_id")
    private List<CompleteOrder> completeOrders;
//    @OneToMany
//    @JoinColumn(name = "courier_id")
//    private List<AssignedOrdersGroup> assignedOrderGroups;
}
