package ru.yandex.yandexlavka.presentation.controllers.orders;

import io.github.bucket4j.Bucket;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.yandexlavka.business.dtos.OrderDTO;
import ru.yandex.yandexlavka.business.services.CompleteOrdersService;
import ru.yandex.yandexlavka.business.services.OrdersService;
import ru.yandex.yandexlavka.presentation.BucketFactory;
import ru.yandex.yandexlavka.presentation.requestmodels.CompleteOrderRequestDto;
import ru.yandex.yandexlavka.presentation.requestmodels.CreateOrderRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
public class OrdersController {
    private final OrdersService ordersService;
    private final CompleteOrdersService completeOrdersService;

    // Для каждого эндпоинта требуется отдельный rate limiter
    // Если их станет значительно больше имеет смысл использовать ConcurrentHashMap
    private final Bucket postOrdersBucket;
    private final Bucket getOrderByIdBucket;
    private final Bucket getOrdersBucket;
    private final Bucket completeOrdersBucket;
    private final Bucket assignOrdersBucket;

    @Autowired
    public OrdersController(CompleteOrdersService completeOrdersService, OrdersService ordersService, BucketFactory bucketFactory) {
        this.completeOrdersService = completeOrdersService;
        this.ordersService = ordersService;
        this.postOrdersBucket = bucketFactory.createBucket(10, 1);
        this.getOrderByIdBucket = bucketFactory.createBucket(10, 1);
        this.getOrdersBucket = bucketFactory.createBucket(10, 1);
        this.completeOrdersBucket = bucketFactory.createBucket(10, 1);
        this.assignOrdersBucket = bucketFactory.createBucket(10, 1);
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
            return ResponseEntity.of(completeOrdersService.completeOrders(completeOrderRequestDto.getCompleteOrders()));
        } else {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
    }

    @GetMapping(path = "/orders/{order_id}", produces = "application/json")
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

    @GetMapping(path = "/orders", produces = "application/json")
    public ResponseEntity<List<OrderDTO>> getOrders(@RequestParam(defaultValue = "1") int limit,
                                                    @RequestParam(defaultValue = "0") int offset) {
        if (limit < 0 || offset < 0) return ResponseEntity.badRequest().build();
        if (getOrdersBucket.tryConsume(1)) {
            return ResponseEntity.ok(ordersService.getOrders(limit, offset));
        } else {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
    }

    @PostMapping(path = "/orders/assign", produces = "application/json")
    // В openapi сказано что параметр date deprecated, оставлен для совместимости
    public ResponseEntity<?> assignOrders(@RequestParam(defaultValue = "now") LocalDate date) {
        if (assignOrdersBucket.tryConsume(1)) {
            return ResponseEntity.badRequest().build(); // TODO
        } else {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
    }
}
