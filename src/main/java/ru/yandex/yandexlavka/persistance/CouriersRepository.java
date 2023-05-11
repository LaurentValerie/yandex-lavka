package ru.yandex.yandexlavka.persistance;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.yandexlavka.business.entities.Courier;

@Repository
public interface CouriersRepository extends PagingAndSortingRepository<Courier, Long>, CrudRepository<Courier, Long>{
}
