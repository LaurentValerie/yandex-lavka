//package ru.yandex.yandexlavka.business.entities;
//
//import com.fasterxml.jackson.annotation.JsonIgnore;
//import com.fasterxml.jackson.annotation.JsonProperty;
//import jakarta.persistence.ElementCollection;
//import jakarta.persistence.Entity;
//import jakarta.persistence.Id;
//import jakarta.persistence.Table;
//import jakarta.validation.constraints.NotNull;
//import lombok.Getter;
//import lombok.Setter;
//
//import java.util.List;
//
//@Getter
//@Setter
//@Entity
//@Table(name = "assigned_orders_groups")
//public class AssignedOrdersGroup {
//    @Id
//    @JsonProperty("group_order_id")
//    private Long groupId;
//    @JsonIgnore
//    private long courier_id;
//    @NotNull
//    @ElementCollection
//    private List<Long> ordersIds;
//}
