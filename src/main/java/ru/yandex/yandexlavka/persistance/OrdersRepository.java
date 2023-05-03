package ru.yandex.yandexlavka.persistance;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.yandex.yandexlavka.business.entities.Order;

import java.util.List;

@Repository
public interface OrdersRepository extends CrudRepository<Order, Long> {
    @Query("SELECT SUM(o.cost) FROM Order o WHERE o.id IN (:ids)")
    int getOrdersTotalCostByIds(@Param("ids") List<Long> ids);
}
