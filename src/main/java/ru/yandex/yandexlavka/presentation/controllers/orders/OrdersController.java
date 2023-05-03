package ru.yandex.yandexlavka.presentation.controllers.orders;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.yandexlavka.business.DTO.OrderDTO;
import ru.yandex.yandexlavka.business.services.CompleteOrdersService;
import ru.yandex.yandexlavka.business.services.OrdersService;
import ru.yandex.yandexlavka.presentation.models.CompleteOrderRequestDto;
import ru.yandex.yandexlavka.presentation.models.CreateOrderRequest;

import java.util.List;
import java.util.Optional;

@RestController
public class OrdersController {
    private final OrdersService ordersService;
    private final CompleteOrdersService completeOrdersService;

    @Autowired
    public OrdersController(CompleteOrdersService completeOrdersService, OrdersService ordersService) {
        this.completeOrdersService = completeOrdersService;
        this.ordersService = ordersService;
    }
    @PostMapping(path = "order", consumes = "application/json", produces = "application/json")
    public ResponseEntity<OrderDTO> postCourier(@RequestBody OrderDTO orderDTO) {
        return ResponseEntity.of(ordersService.saveOrUpdate(orderDTO));
    }

    @PostMapping(path = "orders", consumes = "application/json", produces = "application/json")
    public ResponseEntity<List<OrderDTO>> postCouriers(@RequestBody @Valid CreateOrderRequest createOrderRequest) {
        return ResponseEntity.of(Optional.of(ordersService.saveOrUpdateAll(createOrderRequest.getOrders())));
    }

    @PostMapping(path = "orders/complete", consumes = "application/json", produces = "application/json")
    public ResponseEntity<List<OrderDTO>> completeOrders(@RequestBody @Valid CompleteOrderRequestDto completeOrderRequestDto) {
        return ResponseEntity.of(Optional.of(completeOrdersService.completeOrders(completeOrderRequestDto.getCompleteOrders())));
    }

    @GetMapping("/orders/{order_id}")
    public ResponseEntity<OrderDTO> getCourierById(@PathVariable Long order_id) {
        return ResponseEntity.of(ordersService.getCourierById(order_id));
    }
}
