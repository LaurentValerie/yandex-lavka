package ru.yandex.yandexlavka.business.services.mappers;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.yandex.yandexlavka.business.dtos.OrderDTO;
import ru.yandex.yandexlavka.business.entities.Order;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface DtoToOrderMapper {
    @Mapping(target = "deliveryStart", ignore = true)
    @Mapping(target = "deliveryEnd", ignore = true)
    Order toOrder(OrderDTO orderDTO);

    List<Order> toOrders(List<OrderDTO> orderDTOs);

    @AfterMapping
    default void setDeliveryTimes(@MappingTarget Order order, OrderDTO orderDTO) {
        List<LocalTime> startTimeList = new ArrayList<>();
        List<LocalTime> endTimeList = new ArrayList<>();
        for (String timeRange : orderDTO.getDeliveryHours()) {
            String[] parts = timeRange.split("-");
            LocalTime startTime = LocalTime.parse(parts[0]);
            LocalTime endTime = LocalTime.parse(parts[1]);
            startTimeList.add(startTime);
            endTimeList.add(endTime);
        }
        order.setDeliveryStart(startTimeList);
        order.setDeliveryEnd(endTimeList);
    }
}
