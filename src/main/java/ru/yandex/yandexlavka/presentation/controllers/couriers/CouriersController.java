package ru.yandex.yandexlavka.presentation.controllers.couriers;


import io.github.bucket4j.Bucket;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.yandexlavka.business.DTO.CourierDTO;
import ru.yandex.yandexlavka.business.DTO.CourierMetaInfo;
import ru.yandex.yandexlavka.business.services.CouriersService;
import ru.yandex.yandexlavka.presentation.BucketFactory;
import ru.yandex.yandexlavka.presentation.models.CreateCourierRequest;

import java.util.List;
import java.util.Optional;

@RestController
public class CouriersController {
    private final CouriersService couriersService;
    private final BucketFactory bucketFactory;
    private final Bucket postCourierBucket;
    private final Bucket postCouriersBucket;
    private final Bucket getCourierByIdBucket;
    private final Bucket getCouriersBucket;
    private final Bucket getCourierMetaInfoBucket;


    @Autowired
    public CouriersController(CouriersService couriersService, BucketFactory bucketFactory) {
        this.couriersService = couriersService;
        this.bucketFactory = bucketFactory;
        this.postCourierBucket = bucketFactory.createBucket(10, 1);
        this.postCouriersBucket = bucketFactory.createBucket(10, 1);
        this.getCourierByIdBucket = bucketFactory.createBucket(10, 1);
        this.getCouriersBucket = bucketFactory.createBucket(10, 1);
        this.getCourierMetaInfoBucket = bucketFactory.createBucket(10, 1);
    }

    @PostMapping(path = "courier", consumes = "application/json", produces = "application/json")
    public ResponseEntity<CourierDTO> postCourier(@RequestBody CourierDTO courier) {
        if (postCourierBucket.tryConsume(1)) {
            return ResponseEntity.of(couriersService.saveOrUpdate(courier));
        } else {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
    }

    @PostMapping(path = "couriers", consumes = "application/json", produces = "application/json")
    public ResponseEntity<List<CourierDTO>> postCouriers(@RequestBody @Valid CreateCourierRequest createCourierRequest) {
        if (postCouriersBucket.tryConsume(1)) {
            return ResponseEntity.of(Optional.ofNullable(couriersService.saveOrUpdateAll(createCourierRequest.getCouriers())));
        } else {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
    }

    @GetMapping("/couriers/{courier_id}")
    public ResponseEntity<CourierDTO> getCourierById(@PathVariable Long courier_id) {
        if (getCourierByIdBucket.tryConsume(1)) {
            return ResponseEntity.of(couriersService.getCourierById(courier_id));
        } else {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
    }

    @GetMapping("/couriers")
    public ResponseEntity<List<CourierDTO>> getCouriers(@RequestParam(defaultValue = "1") int limit,
                                                        @RequestParam(defaultValue = "0") int offset) {
        if (getCouriersBucket.tryConsume(1)) {
            return ResponseEntity.ok(couriersService.getCouriers(limit, offset));
        } else {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
    }

    @GetMapping(path = "/couriers/meta-info/{courier_id}")
    public ResponseEntity<CourierMetaInfo> getCourierMetaInfo(@PathVariable long courier_id,
                                                              @RequestParam String startDate,
                                                              @RequestParam String endDate) {
        if (getCourierMetaInfoBucket.tryConsume(1)) {
            return ResponseEntity.of(couriersService.getCourierMetaInfo(courier_id, startDate, endDate));
        } else {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
    }
}
