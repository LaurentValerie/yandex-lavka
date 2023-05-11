package ru.yandex.yandexlavka.business.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Entity
public class AssignedOrdersGroup {
    @Id
    @JsonProperty("group_order_id")
    private Long groupId;
    @NotNull
    @ElementCollection
    private List<Order> orders;
}
