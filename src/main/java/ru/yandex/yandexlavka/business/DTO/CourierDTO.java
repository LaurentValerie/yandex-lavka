package ru.yandex.yandexlavka.business.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CourierDTO {
    @JsonProperty("courier_id")
    private Long id;
    @NotNull
    @JsonProperty("courier_type")
    private CourierType courierType;
    @NotEmpty
    @ElementCollection
    private List<Integer> regions;
    @NotEmpty
    @ElementCollection
    @JsonProperty("working_hours")
    private List<@Pattern(regexp = "^(0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]-(0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$")
                String> workingHours;
}
