package ru.yandex.yandexlavka.persistance;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.yandex.yandexlavka.business.entities.Courier;

import java.util.List;

@Repository
public interface CouriersRepository extends PagingAndSortingRepository<Courier, Long>, CrudRepository<Courier, Long>{
    @Query("SELECT c FROM Courier c ORDER BY c.id OFFSET :offset ROWS FETCH NEXT :limit ROWS ONLY")
    List<Courier> findCouriersWithLimitAndOffset(@Param("offset") int offset, @Param("limit") int limit);
}
