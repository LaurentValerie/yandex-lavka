package ru.yandex.yandexlavka.business.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.yandexlavka.business.dtos.OrderDTO;
import ru.yandex.yandexlavka.business.entities.CompleteOrder;
import ru.yandex.yandexlavka.business.entities.Courier;
import ru.yandex.yandexlavka.business.entities.Order;
import ru.yandex.yandexlavka.business.services.mappers.OrderToDtoMapper;
import ru.yandex.yandexlavka.persistance.CompleteOrdersRepository;
import ru.yandex.yandexlavka.persistance.CouriersRepository;
import ru.yandex.yandexlavka.persistance.OrdersRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CompleteOrdersService {
    private final CompleteOrdersRepository completeOrdersRepository;
    private final CouriersRepository couriersRepository;
    private final OrdersRepository ordersRepository;
    private final OrderToDtoMapper orderToDtoMapper;

    @Autowired
    CompleteOrdersService(CompleteOrdersRepository completeOrdersRepository,
                          CouriersRepository couriersRepository, OrdersRepository ordersRepository,
                          OrderToDtoMapper orderToDtoMapper) {
        this.completeOrdersRepository = completeOrdersRepository;
        this.couriersRepository = couriersRepository;
        this.ordersRepository = ordersRepository;
        this.orderToDtoMapper = orderToDtoMapper;
    }

    @Transactional
    public Optional<List<OrderDTO>> completeOrders(List<CompleteOrder> completeOrders) {
        // Выбираем сохраненные заказы по id
        List<Long> orderIds = new ArrayList<>();
        for (CompleteOrder completeOrder : completeOrders) {
            // Проверяем существую ли такие заказ и курьер
            Optional<Order> order = ordersRepository.findById(completeOrder.getOrderID());
            Optional<Courier> courier = couriersRepository.findById(completeOrder.getCourierID());
            if (order.isEmpty() || courier.isEmpty()) return Optional.empty();

            orderIds.add(completeOrder.getOrderID());
        }
        completeOrdersRepository.saveAll(completeOrders);
        List<Order> orders = (List<Order>) ordersRepository.findAllById(orderIds);

        // Обновляем время выполнения заказов
        for (int i = 0; i < completeOrders.size(); i++) {
            orders.get(i).setCompletedTime(completeOrders.get(i).getCompleteTime());
        }
        ordersRepository.saveAll(orders);

        return Optional.of(orderToDtoMapper.toDtos(orders));
    }
}
