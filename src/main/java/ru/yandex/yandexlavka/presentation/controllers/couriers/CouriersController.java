package ru.yandex.yandexlavka.presentation.controllers.couriers;


import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.yandexlavka.business.DTO.CourierDTO;
import ru.yandex.yandexlavka.business.DTO.CourierMetaInfo;
import ru.yandex.yandexlavka.business.services.CouriersService;
import ru.yandex.yandexlavka.presentation.models.CreateCourierRequest;

import java.util.List;
import java.util.Optional;

@RestController
public class CouriersController {
    private final CouriersService couriersService;

    @Autowired
    public CouriersController(CouriersService couriersService) {
        this.couriersService = couriersService;
    }

    @PostMapping(path = "courier", consumes = "application/json", produces = "application/json")
    public ResponseEntity<CourierDTO> postCourier(@RequestBody CourierDTO courier) {
        return ResponseEntity.of(couriersService.saveOrUpdate(courier));
    }

    @PostMapping(path = "couriers", consumes = "application/json", produces = "application/json")
    public ResponseEntity<List<CourierDTO>> postCouriers(@RequestBody @Valid CreateCourierRequest createCourierRequest) {
        return ResponseEntity.of(Optional.ofNullable(couriersService.saveOrUpdateAll(createCourierRequest.getCouriers())));
    }

    @GetMapping("/couriers/{courier_id}")
    public ResponseEntity<CourierDTO> getCourierById(@PathVariable Long courier_id) {
        return ResponseEntity.of(couriersService.getCourierById(courier_id));
    }

    @GetMapping("/couriers")
    public ResponseEntity<List<CourierDTO>> getCouriers(@RequestParam(defaultValue = "1") int limit,
                                                        @RequestParam(defaultValue = "0") int offset) {
        return ResponseEntity.badRequest().build();
    }

    @GetMapping(path = "/couriers/meta-info/{courier_id}")
    public ResponseEntity<CourierMetaInfo> getCourierMetaInfo(@PathVariable long courier_id,
                                                              @RequestParam String startDate,
                                                              @RequestParam String endDate) {
        return ResponseEntity.of(couriersService.getCourierMetaInfo(courier_id, startDate, endDate));
    }
}
