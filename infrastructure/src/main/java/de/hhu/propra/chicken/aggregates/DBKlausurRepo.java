package de.hhu.propra.chicken.aggregates;

import de.hhu.propra.chicken.util.KlausurReferenz;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface DBKlausurRepo extends CrudRepository<KlausurEntity, Long> {
//    @Query("SELECT * FROM chicken.klausur WHERE lsf_id = :lsfId")
//    Optional<KlausurEntity> findByLsfId(@Param("lsfId") Long lsfId);

    Optional<KlausurEntity> findByLsfId(Long lsfId);

//    @Query("SELECT klausur_id FROM chicken.student_belegt_klausur WHERE student_id = :id")
//    Set<KlausurReferenz> findKlausurIdsByStudentId(@Param("id") Long id);


    @Query("SELECT k.id, k.lsf_id, k.name, k.start, k.ende, k.typ FROM chicken.student_belegt_klausur AS conj\n" +
            "JOIN chicken.klausur AS k ON conj.klausur_id = k.id\n" +
            "WHERE conj.id = :studentId")
    Set<KlausurEntity> findAllByStudentId(@Param("studentId")Long studentId);


//    @Override
//    @Query("SELECT * FROM chicken.klausur WHERE id IN (:ids)")
//    Set<KlausurEntity> findAllById(@Param("ids")Iterable<Long> ids);

}
