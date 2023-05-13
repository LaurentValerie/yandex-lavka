package ru.yandex.yandexlavka.business.services.mappers;

import org.mapstruct.*;
import ru.yandex.yandexlavka.business.dtos.CourierDTO;
import ru.yandex.yandexlavka.business.entities.Courier;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CourierToDtoMapper {
    StringBuilder sb = new StringBuilder();

    @Mapping(target = "workingHours", ignore = true)
    CourierDTO toDto(Courier courier);

    List<CourierDTO> toDtos(List<Courier> couriers);

    @AfterMapping
    default void setWorkingHoursString(@MappingTarget CourierDTO courierDTO, Courier courier) {
        List<LocalTime> startTime = courier.getStartTimeList();
        List<LocalTime> endTime = courier.getEndTimeList();
        List<String> ranges = new ArrayList<>();
        for (int i = 0; i < endTime.size(); i++) {
            sb.append(startTime.get(i)).append("-")
                    .append(endTime.get(i));
            ranges.add(sb.toString());
            sb.setLength(0);
        }
        courierDTO.setWorkingHours(ranges);
    }
}
