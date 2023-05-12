package ru.yandex.yandexlavka.business.services;

import org.modelmapper.ModelMapper;
import ru.yandex.yandexlavka.business.dtos.CourierDTO;
import ru.yandex.yandexlavka.business.dtos.OrderDTO;
import ru.yandex.yandexlavka.business.entities.Courier;
import ru.yandex.yandexlavka.business.entities.Order;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Deprecated
public class Mappers {
    private static final ModelMapper mapper = new ModelMapper();
    private static final StringBuilder sb = new StringBuilder();

    static Courier convertDTOtoCourier(CourierDTO courierDTO) {
        Courier courier = mapper.map(courierDTO, Courier.class);
        List<LocalTime> startTimeList = new ArrayList<>();
        List<LocalTime> endTimeList = new ArrayList<>();
        for (String timeRange : courierDTO.getWorkingHours()) {
            String[] parts = timeRange.split("-");
            LocalTime startTime = LocalTime.parse(parts[0]);
            LocalTime endTime = LocalTime.parse(parts[1]);
            startTimeList.add(startTime);
            endTimeList.add(endTime);
        }
        courier.setStartTimeList(startTimeList);
        courier.setEndTimeList(endTimeList);
        return courier;
    }

    static CourierDTO convertCourierToDTO(Courier courier) {
        CourierDTO courierDTO = mapper.map(courier, CourierDTO.class);
        List<String> ranges = new ArrayList<>();
        List<LocalTime> startTime = courier.getStartTimeList();
        List<LocalTime> endTime = courier.getEndTimeList();
        for (int i = 0; i < endTime.size(); i++) {
            sb.append(startTime.get(i)).append("-")
                    .append(endTime.get(i));
            ranges.add(sb.toString());
            sb.setLength(0);
        }
        courierDTO.setWorkingHours(ranges);
        return courierDTO;
    }

    static Order convertDTOtoOrder(OrderDTO orderDTO) {
        Order order = mapper.map(orderDTO, Order.class);
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
        return order;
    }

    static OrderDTO convertOrderToDTO(Order order) {
        OrderDTO orderDTO = mapper.map(order, OrderDTO.class);
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
        return orderDTO;
    }
}
