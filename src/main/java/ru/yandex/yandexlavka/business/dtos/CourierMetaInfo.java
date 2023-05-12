package ru.yandex.yandexlavka.business.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CourierMetaInfo {
    @JsonProperty("courier_id")
    private long id;
    @JsonProperty("courier_type")
    private CourierType courierType;
    private List<Integer> regions;
    @JsonProperty("working_hours")
    private List<String> workingHours;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int rating;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int earnings;
}
