package ru.yandex.yandexlavka.business.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.yandexlavka.business.DTO.CourierDTO;
import ru.yandex.yandexlavka.business.DTO.CourierMetaInfo;
import ru.yandex.yandexlavka.business.DTO.CourierType;
import ru.yandex.yandexlavka.business.entities.Courier;
import ru.yandex.yandexlavka.persistance.CompleteOrdersRepository;
import ru.yandex.yandexlavka.persistance.CouriersRepository;
import ru.yandex.yandexlavka.persistance.OrdersRepository;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CouriersService {
    private final CompleteOrdersRepository completeOrdersRepository;
    private final CouriersRepository couriersRepository;
    private final OrdersRepository ordersRepository;

    @Autowired
    CouriersService(CompleteOrdersRepository completeOrdersRepository,
                          CouriersRepository couriersRepository,
                          OrdersRepository ordersRepository) {
        this.completeOrdersRepository = completeOrdersRepository;
        this.couriersRepository = couriersRepository;
        this.ordersRepository = ordersRepository;
    }

    public Optional<CourierDTO> saveOrUpdate(CourierDTO courierDTO) {
        Courier courier = Mappers.convertDTOtoCourier(courierDTO);
        return Optional.of(Mappers.convertCourierToDTO(couriersRepository.save(courier)));
    }

    @Transactional
    public List<CourierDTO> saveOrUpdateAll(List<CourierDTO> couriersDTO) {
        List<Courier> couriers = new ArrayList<>();
        for (CourierDTO courierDTO : couriersDTO) {
            couriers.add(Mappers.convertDTOtoCourier(courierDTO));
        }
        couriers = (List<Courier>) couriersRepository.saveAll(couriers);
        List<CourierDTO> response = new ArrayList<>();
        for (Courier courier : couriers) {
            response.add(Mappers.convertCourierToDTO(courier));
        }
        return response;
    }

    public Optional<CourierDTO> getCourierById(Long id) {
        return couriersRepository.findById(id).map(Mappers::convertCourierToDTO);
    }

    public Optional<CourierMetaInfo> getCourierMetaInfo(long id, String startDate, String endDate) {
        CourierMetaInfo courierMetaInfo;

        Courier courier = couriersRepository.findById(id).get();
        CourierDTO courierDTO = Mappers.convertCourierToDTO(courier);
        CourierType courierType = courier.getCourierType();

        Instant start = Instant.parse(startDate);
        Instant end = Instant.parse(endDate);

        List<Long> completeOrdersIds = completeOrdersRepository.findOrderIdsByCompleteTimeAndCourierId(id, start, end);
        int completeOrders = completeOrdersIds.size();

        if (completeOrders != 0) {
            int earnings = getCourierEarnings(courierType, completeOrdersIds);
            int rating = getCourierRating(courierType, completeOrders, start, end);
            courierMetaInfo = new CourierMetaInfo(id, courierType, courier.getRegions(), courierDTO.getWorkingHours(), rating, earnings);
        } else {
            courierMetaInfo = new CourierMetaInfo(id, courierType, null, null, 0, 0);
        }

        return Optional.of(courierMetaInfo);
    }

    private int getCourierEarnings(CourierType courierType, List<Long> completeOrdersIds) {
        int sum = ordersRepository.getOrdersTotalCostByIds(completeOrdersIds);
        int C = switch (courierType) {
            case FOOT -> 2;
            case BIKE -> 3;
            case AUTO -> 4;
        };
        return sum * C;
    }

    private int getCourierRating(CourierType courierType, int completeOrders, Instant start, Instant end) {
        Duration duration = Duration.between(start, end);
        int hours = (int) duration.toHours();
        int C = switch (courierType) {
            case FOOT -> 3;
            case BIKE -> 2;
            case AUTO -> 1;
        };
        return (hours / completeOrders) * C;
    }
}