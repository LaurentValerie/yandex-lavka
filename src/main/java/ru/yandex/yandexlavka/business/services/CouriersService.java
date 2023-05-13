package ru.yandex.yandexlavka.business.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.yandex.yandexlavka.business.dtos.CourierDTO;
import ru.yandex.yandexlavka.business.dtos.CourierMetaInfo;
import ru.yandex.yandexlavka.business.dtos.CourierType;
import ru.yandex.yandexlavka.business.entities.Courier;
import ru.yandex.yandexlavka.business.services.mappers.CourierToDtoMapper;
import ru.yandex.yandexlavka.business.services.mappers.DtoToCourierMapper;
import ru.yandex.yandexlavka.persistance.CompleteOrdersRepository;
import ru.yandex.yandexlavka.persistance.CouriersRepository;
import ru.yandex.yandexlavka.persistance.OrdersRepository;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Service
public class CouriersService {
    private final CompleteOrdersRepository completeOrdersRepository;
    private final CouriersRepository couriersRepository;
    private final OrdersRepository ordersRepository;
    private final CourierToDtoMapper courierToDtoMapper;
    private final DtoToCourierMapper dtoToCourierMapper;

    @Autowired
    CouriersService(CompleteOrdersRepository completeOrdersRepository,
                    CouriersRepository couriersRepository,
                    OrdersRepository ordersRepository,
                    CourierToDtoMapper courierToDtoMapper, DtoToCourierMapper dtoToCourierMapper) {
        this.completeOrdersRepository = completeOrdersRepository;
        this.couriersRepository = couriersRepository;
        this.ordersRepository = ordersRepository;
        this.courierToDtoMapper = courierToDtoMapper;
        this.dtoToCourierMapper = dtoToCourierMapper;
    }

    public Optional<CourierDTO> saveOrUpdate(CourierDTO courierDTO) {
        Courier courier = dtoToCourierMapper.DtoToCourier(courierDTO);
//        Courier courier = Mappers.convertDTOtoCourier(courierDTO);
        var saved = couriersRepository.save(courier);
        return Optional.of(courierToDtoMapper.toDto(saved));
    }

    @Transactional
    public List<CourierDTO> saveOrUpdateAll(List<CourierDTO> couriersDTO) {
        List<Courier> couriers = dtoToCourierMapper.DtosToCouriers(couriersDTO);
//        List<Courier> couriers = new ArrayList<>();
//        for (CourierDTO courierDTO : couriersDTO) {
//            couriers.add(Mappers.convertDTOtoCourier(courierDTO));
//        }
        couriers = (List<Courier>) couriersRepository.saveAll(couriers);
//        List<CourierDTO> response = new ArrayList<>();
//        for (Courier courier : couriers) {
//            response.add(Mappers.convertCourierToDTO(courier));
//        }
        return courierToDtoMapper.toDtos(couriers);
    }

    public Optional<CourierDTO> getCourierById(Long id) {
        return couriersRepository.findById(id).map(courierToDtoMapper::toDto);
//        return couriersRepository.findById(id).map(Mappers::convertCourierToDTO);
    }

    public List<CourierDTO> getCouriers(int limit, int offset) {

        // Более классическая версия offset, сдвиг по страницам
//        Pageable pageable = PageRequest.of(offset, limit);
//        List<Courier> couriers = couriersRepository.findAll(pageable).getContent();

        // По заданию offset - Количество курьеров, которое нужно пропустить для отображения текущей страницы
        List<Courier> couriers = couriersRepository.findCouriersWithLimitAndOffset(offset, limit);
        return courierToDtoMapper.toDtos(couriers);
    }

    @Transactional
    public Optional<CourierMetaInfo> getCourierMetaInfo(long id, String startDate, String endDate) {
        Optional<Courier> courierOptional = couriersRepository.findById(id);
        if (courierOptional.isEmpty()) {
            return Optional.empty();
        }
        Courier courier = courierOptional.get();
        CourierMetaInfo courierMetaInfo;
        CourierDTO courierDTO = courierToDtoMapper.toDto(courier);
//        CourierDTO courierDTO = Mappers.convertCourierToDTO(courier);
        CourierType courierType = courier.getCourierType();

        LocalDate startD = LocalDate.parse(startDate);
        LocalDate endD = LocalDate.parse(endDate);
        Instant start = startD.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant end = endD.atStartOfDay(ZoneId.systemDefault()).toInstant();
//        Instant start = Instant.parse(startDate);
//        Instant end = Instant.parse(endDate);

        List<Long> completeOrdersIds = completeOrdersRepository.findOrderIdsByCompleteTimeAndCourierId(id, start, end);
        int completeOrders = completeOrdersIds.size();

        if (completeOrders != 0) {
            int earnings = getCourierEarnings(courierType, completeOrdersIds);
            int rating = getCourierRating(courierType, completeOrders, start, end);
            courierMetaInfo = new CourierMetaInfo(id, courierType, courier.getRegions(), courierDTO.getWorkingHours(), rating, earnings);
        } else {
            courierMetaInfo = new CourierMetaInfo(id, courierType, courier.getRegions(), courierDTO.getWorkingHours(), 0, 0);
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
