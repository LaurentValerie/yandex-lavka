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
public interface OrderToDtoMapper {
    StringBuilder sb = new StringBuilder();

    @Mapping(target = "deliveryHours", ignore = true)
    OrderDTO toDto(Order order);

    List<OrderDTO> toDtos(List<Order> orders);

    @AfterMapping
    default void setDeliveryHours(@MappingTarget OrderDTO orderDTO, Order order) {
        List<String> ranges = new ArrayList<>();
        List<LocalTime> startTime = order.getDeliveryStart();
        List<LocalTime> endTime = order.getDeliveryEnd();
        for (int i = 0; i < endTime.size(); i++) {
            sb.append(startTime.get(i)).append("-")
                    .append(endTime.get(i));
            ranges.add(sb.toString());
            sb.setLength(0);
        }
        orderDTO.setDeliveryHours(ranges);
    }
}
