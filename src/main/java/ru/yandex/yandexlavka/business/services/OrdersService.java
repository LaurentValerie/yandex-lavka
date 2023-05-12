package ru.yandex.yandexlavka.business.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.yandex.yandexlavka.business.dtos.OrderDTO;
import ru.yandex.yandexlavka.business.entities.Order;
import ru.yandex.yandexlavka.business.services.mappers.DtoToOrderMapper;
import ru.yandex.yandexlavka.business.services.mappers.OrderToDtoMapper;
import ru.yandex.yandexlavka.persistance.CouriersRepository;
import ru.yandex.yandexlavka.persistance.OrdersRepository;

import java.util.List;
import java.util.Optional;

@Service
public class OrdersService {
    private final CouriersRepository couriersRepository;
    private final OrdersRepository ordersRepository;
    private final OrderToDtoMapper orderToDtoMapper;
    private final DtoToOrderMapper dtoToOrderMapper;

    @Autowired
    public OrdersService(CouriersRepository couriersRepository, OrdersRepository ordersRepository,
                         OrderToDtoMapper orderToDtoMapper, DtoToOrderMapper dtoToOrderMapper) {
        this.couriersRepository = couriersRepository;
        this.ordersRepository = ordersRepository;
        this.orderToDtoMapper = orderToDtoMapper;
        this.dtoToOrderMapper = dtoToOrderMapper;
    }

    public Optional<OrderDTO> saveOrUpdate(OrderDTO orderDTO) {
        Order order = dtoToOrderMapper.toOrder(orderDTO);
        var saved = ordersRepository.save(order);
        return Optional.ofNullable(orderToDtoMapper.toDto(saved));
//        Order order = Mappers.convertDTOtoOrder(orderDTO);
//        return Optional.of(Mappers.convertOrderToDTO(ordersRepository.save(order)));
    }

    @Transactional
    public List<OrderDTO> saveOrUpdateAll(List<OrderDTO> orderDTOs) {
        List<Order> orders = dtoToOrderMapper.toOrders(orderDTOs);
//        List<Order> orders = new ArrayList<>();
//        for (OrderDTO orderDTO : ordersDTO) {
//            orders.add(Mappers.convertDTOtoOrder(orderDTO));
//        }
        orders = (List<Order>) ordersRepository.saveAll(orders);
        List<OrderDTO> response = orderToDtoMapper.toDtos(orders);
//        List<OrderDTO> response = new ArrayList<>();
//        for (Order order : orders) {
//            response.add(Mappers.convertOrderToDTO(order));
//        }
        return response;
    }

    public Optional<OrderDTO> getOrderById(Long id) {
        return ordersRepository.findById(id).map(orderToDtoMapper::toDto);
//        return ordersRepository.findById(id).map(Mappers::convertOrderToDTO);
    }

    public List<OrderDTO> getOrders(int limit, int offset) {
        Pageable pageable = PageRequest.of(offset, limit);
        List<Order> orders = ordersRepository.findAll(pageable).getContent();
        List<OrderDTO> ordersDTOs = orderToDtoMapper.toDtos(orders);
//        var orders = ordersRepository.findAll(pageable);
//        List<OrderDTO> ordersDTO = new ArrayList<>(orders.getSize());
//        for (Order order: orders) {
//            ordersDTO.add(Mappers.convertOrderToDTO(order));
//        }
        return ordersDTOs;
    }

//    public List<?> assignOrders(LocalDate localDate) {
//        List<Order> orders = (List<Order>) ordersRepository.findAll();
//        List<Courier> couriers = (List<Courier>) couriersRepository.findAll();
//        for (Order order : orders) {
//            for (Courier courier : couriers) {
//
//            }
//        }
//
//        return null;
//    }
//    public List<Courier> findMatchingCouriers(Order order) {
//        List<Courier> matchingCouriers = new ArrayList<>();
//
//        // Find all couriers that can deliver to the order's region
//        List<Courier> couriersInRegion = (List<Courier>) couriersRepository.findAll();
//
//        for (Courier courier : couriersInRegion) {
//            // Check if the courier's delivery start times overlap with the order's delivery start times
//            List<LocalTime> courierStartTimes = courier.getStartTimeList();
//            List<LocalTime> courierEndTimes = courier.getEndTimeList();
//            List<LocalTime> orderStartTimes = order.getDeliveryStart();
//            List<LocalTime> orderEndTimes = order.getDeliveryEnd();
//
//            boolean overlap = false;
//            for (int i = 0; i < courierStartTimes.size(); i++) {
//                LocalTime courierStartTime = courierStartTimes.get(i);
//                LocalTime courierEndTime = courierEndTimes.get(i);
//
//                for (int j = 0; j < orderStartTimes.size(); j++) {
//                    LocalTime orderStartTime = orderStartTimes.get(j);
//                    LocalTime orderEndTime = orderEndTimes.get(j);
//
//                    if (orderStartTime.isBefore(courierEndTime) && orderEndTime.isAfter(courierStartTime)) {
//                        switch (courier.getCourierType()) {
//                            case FOOT -> {
//                                int currOrderNumber = courier.getCurOrderNumber().get(i);
//                                if (currOrderNumber == 2) {
//                                    LocalTime newStartTime =
//                                    courier.getStartTimeList().set(i, orderStartTime.plusMinutes(25));
//                                }
//                            }
//                            case BIKE -> System.out.println();
//                            case AUTO -> System.out.println();
//                        }
//                    }
//                }
//
//                if (overlap) {
//                    break;
//                }
//            }
//
//            if (overlap) {
//                // Check if the courier can carry the weight of the order
//                if (courier.getCourierType().getMaxWeight() >= order.getWeight()) {
//                    matchingCouriers.add(courier);
//                }
//            }
//        }
//
//        return matchingCouriers;
//    }
}
