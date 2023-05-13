package ru.yandex.yandexlavka.business.services.mappers;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.yandex.yandexlavka.business.dtos.CourierDTO;
import ru.yandex.yandexlavka.business.entities.Courier;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface DtoToCourierMapper {
    @Mapping(target = "completeOrders", ignore = true)
    @Mapping(target = "startTimeList", ignore = true)
    @Mapping(target = "endTimeList", ignore = true)
    Courier DtoToCourier(CourierDTO courierDTO);

    List<Courier> DtosToCouriers(List<CourierDTO> courierDTOs);

    @AfterMapping
    default void setBookAuthor(@MappingTarget Courier courier, CourierDTO courierDTO) {
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
    }
}
