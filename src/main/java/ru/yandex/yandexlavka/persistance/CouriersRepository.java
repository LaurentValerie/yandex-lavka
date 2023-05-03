package ru.yandex.yandexlavka.persistance;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.yandexlavka.business.DTO.CourierType;
import ru.yandex.yandexlavka.business.entities.Courier;

@Repository
public interface CouriersRepository extends CrudRepository<Courier, Long>{
}
