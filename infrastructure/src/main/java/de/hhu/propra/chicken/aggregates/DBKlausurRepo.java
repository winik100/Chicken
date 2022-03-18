package de.hhu.propra.chicken.aggregates;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DBKlausurRepo extends CrudRepository<KlausurEntity, Long> {
    @Query("SELECT * FROM klausur WHERE lsf_id = :lsfId")
    Optional<KlausurEntity> findByLsfId(@Param("lsfId") Long lsfId);
}
