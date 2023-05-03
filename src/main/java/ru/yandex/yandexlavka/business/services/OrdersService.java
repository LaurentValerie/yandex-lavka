package ru.yandex.yandexlavka.business.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.yandexlavka.business.DTO.OrderDTO;
import ru.yandex.yandexlavka.business.entities.Order;
import ru.yandex.yandexlavka.persistance.OrdersRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrdersService {
    private final OrdersRepository ordersRepository;

    @Autowired
    public OrdersService(OrdersRepository ordersRepository) {
        this.ordersRepository = ordersRepository;
    }

    public Optional<OrderDTO> saveOrUpdate(OrderDTO orderDTO) {
        Order order = Mappers.convertDTOtoOrder(orderDTO);
        return Optional.of(Mappers.convertOrderToDTO(ordersRepository.save(order)));
    }

    @Transactional
    public List<OrderDTO> saveOrUpdateAll(List<OrderDTO> ordersDTO) {
        List<Order> orders = new ArrayList<>();
        for (OrderDTO orderDTO : ordersDTO) {
            orders.add(Mappers.convertDTOtoOrder(orderDTO));
        }
        orders = (List<Order>) ordersRepository.saveAll(orders);
        List<OrderDTO> response = new ArrayList<>();
        for (Order order : orders) {
            response.add(Mappers.convertOrderToDTO(order));
        }
        return response;
    }

    public Optional<OrderDTO> getCourierById(Long id) {
        return ordersRepository.findById(id).map(Mappers::convertOrderToDTO);
    }
}
