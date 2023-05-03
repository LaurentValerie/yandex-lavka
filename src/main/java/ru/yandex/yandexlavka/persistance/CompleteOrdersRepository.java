package ru.yandex.yandexlavka.persistance;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.yandex.yandexlavka.business.entities.CompleteOrder;

import java.time.Instant;
import java.util.List;

@Repository
public interface CompleteOrdersRepository extends CrudRepository<CompleteOrder, Long> {
    @Query("SELECT c.orderID FROM CompleteOrder c WHERE c.courierID = :id AND c.completeTime >= :startDate AND c.completeTime < :endDate")
    List<Long> findOrderIdsByCompleteTimeAndCourierId(@Param("id") Long courierId, @Param("startDate") Instant startDate, @Param("endDate") Instant endDate);
    int countAllByCourierIDEqualsAndCompleteTimeGreaterThanEqualAndCompleteTimeLessThan(Long courierId, Instant startDate, Instant endDate);

}
