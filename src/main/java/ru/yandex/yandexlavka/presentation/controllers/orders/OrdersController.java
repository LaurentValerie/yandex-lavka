package ru.yandex.yandexlavka.presentation.controllers.orders;

import io.github.bucket4j.Bucket;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.yandexlavka.business.DTO.OrderDTO;
import ru.yandex.yandexlavka.business.services.CompleteOrdersService;
import ru.yandex.yandexlavka.business.services.OrdersService;
import ru.yandex.yandexlavka.presentation.BucketFactory;
import ru.yandex.yandexlavka.presentation.models.CompleteOrderRequestDto;
import ru.yandex.yandexlavka.presentation.models.CreateOrderRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
public class OrdersController {
    private final OrdersService ordersService;
    private final CompleteOrdersService completeOrdersService;
    private final BucketFactory bucketFactory;
    private final Bucket postOrderBucket;
    private final Bucket postOrdersBucket;
    private final Bucket getOrderByIdBucket;
    private final Bucket getOrdersBucket;
    private final Bucket completeOrdersBucket;

    @Autowired
    public OrdersController(CompleteOrdersService completeOrdersService, OrdersService ordersService, BucketFactory bucketFactory) {
        this.completeOrdersService = completeOrdersService;
        this.ordersService = ordersService;
        this.bucketFactory = bucketFactory;
        this.postOrderBucket = bucketFactory.createBucket(10, 1);
        this.postOrdersBucket = bucketFactory.createBucket(10, 1);
        this.getOrderByIdBucket = bucketFactory.createBucket(10, 1);
        this.getOrdersBucket = bucketFactory.createBucket(10, 1);
        this.completeOrdersBucket = bucketFactory.createBucket(10, 1);
    }

    @PostMapping(path = "order", consumes = "application/json", produces = "application/json")
    public ResponseEntity<OrderDTO> postOrder(@RequestBody OrderDTO orderDTO) {
        if (postOrderBucket.tryConsume(1)) {
            return ResponseEntity.of(ordersService.saveOrUpdate(orderDTO));
        } else {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
    }

    @PostMapping(path = "orders", consumes = "application/json", produces = "application/json")
    public ResponseEntity<List<OrderDTO>> postOrders(@RequestBody @Valid CreateOrderRequest createOrderRequest) {
        if (postOrdersBucket.tryConsume(1)) {
            return ResponseEntity.of(Optional.of(ordersService.saveOrUpdateAll(createOrderRequest.getOrders())));
        } else {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
    }

    @PostMapping(path = "orders/complete", consumes = "application/json", produces = "application/json")
    public ResponseEntity<List<OrderDTO>> completeOrders(@RequestBody @Valid CompleteOrderRequestDto completeOrderRequestDto) {
        if (completeOrdersBucket.tryConsume(1)) {
            return ResponseEntity.of(Optional.of(completeOrdersService.completeOrders(completeOrderRequestDto.getCompleteOrders())));
        } else {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
    }

    @GetMapping("/orders/{order_id}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long order_id) {
        if (getOrderByIdBucket.tryConsume(1)) {
            Optional<OrderDTO> order = ordersService.getOrderById(order_id);
            if (order.isPresent()) {
                return ResponseEntity.of(ordersService.getOrderById(order_id));
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderDTO>> getOrders(@RequestParam(defaultValue = "1") int limit,
                                                    @RequestParam(defaultValue = "0") int offset) {
        if (getOrdersBucket.tryConsume(1)) {
            return ResponseEntity.ok(ordersService.getOrders(limit, offset));
        } else {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
    }

    @PostMapping("/orders/assign")
    public ResponseEntity<?> assignOrders(@RequestParam(defaultValue = "now") LocalDate localDate) {
        return ResponseEntity.badRequest().build();
    }
}
