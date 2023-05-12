package ru.yandex.yandexlavka.business.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.yandexlavka.business.dtos.OrderDTO;
import ru.yandex.yandexlavka.business.entities.CompleteOrder;
import ru.yandex.yandexlavka.business.entities.Order;
import ru.yandex.yandexlavka.business.services.mappers.OrderToDtoMapper;
import ru.yandex.yandexlavka.persistance.CompleteOrdersRepository;
import ru.yandex.yandexlavka.persistance.CouriersRepository;
import ru.yandex.yandexlavka.persistance.OrdersRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class CompleteOrdersService {
    private final CompleteOrdersRepository completeOrdersRepository;
    private final CouriersRepository couriersRepository;
    private final OrdersRepository ordersRepository;
    private final OrderToDtoMapper orderToDtoMapper;

    @Autowired
    CompleteOrdersService(CompleteOrdersRepository completeOrdersRepository,
                          CouriersRepository couriersRepository,
                          OrdersRepository ordersRepository, OrderToDtoMapper orderToDtoMapper) {
        this.completeOrdersRepository = completeOrdersRepository;
        this.couriersRepository = couriersRepository;
        this.ordersRepository = ordersRepository;
        this.orderToDtoMapper = orderToDtoMapper;
    }

    @Transactional
    public List<OrderDTO> completeOrders(List<CompleteOrder> completeOrders) {
        completeOrdersRepository.saveAll(completeOrders);
        List<Long> orderIds = new ArrayList<>();
        for (CompleteOrder completeOrder : completeOrders) {
            orderIds.add(completeOrder.getOrderID());
        }
        List<Order> orders = (List<Order>) ordersRepository.findAllById(orderIds);
        for (int i = 0; i < completeOrders.size(); i++) {
            orders.get(i).setCompletedTime(completeOrders.get(i).getCompleteTime());
//            Order tmp = orders.get(i);
//            tmp.setCompletedTime(completeOrders.get(i).getCompleteTime());
//            orders.set(i, tmp);
        }
        ordersRepository.saveAll(orders);
        List<OrderDTO> orderDTOs = orderToDtoMapper.toDtos(orders);
//        List<OrderDTO> ordersDTO = new ArrayList<>();
//        for (Order order : orders) {
//            ordersDTO.add(Mappers.convertOrderToDTO(order));
//        }
        return orderDTOs;
    }
}
