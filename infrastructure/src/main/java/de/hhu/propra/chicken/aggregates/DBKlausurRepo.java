package de.hhu.propra.chicken.aggregates;

import de.hhu.propra.chicken.util.KlausurReferenz;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface DBKlausurRepo extends CrudRepository<KlausurEntity, Long> {
    @Query("SELECT * FROM chicken.klausur WHERE lsf_id = :lsfId")
    Optional<KlausurEntity> findByLsfId(@Param("lsfId") Long lsfId);

    @Query("SELECT klausur_id FROM chicken.student_belegt_klausur WHERE student_id = :id")
    Set<KlausurReferenz> findKlausurIdsByStudentId(@Param("id") Long id);

    @Override
    @Query("SELECT * FROM chicken.klausur WHERE id IN (:ids)")
    Iterable<KlausurEntity> findAllById(@Param("ids")Iterable<Long> ids);
}
