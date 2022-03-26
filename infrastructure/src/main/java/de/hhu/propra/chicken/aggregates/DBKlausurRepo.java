package de.hhu.propra.chicken.aggregates;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface DBKlausurRepo extends CrudRepository<KlausurEntity, Long> {

    Optional<KlausurEntity> findByLsfId(Long lsfId);

    @Query("SELECT k.id, k.lsf_id, k.name, k.start, k.ende, k.typ FROM student_belegt_klausur AS conj\n" +
            "JOIN klausur AS k ON conj.klausur_id = k.id\n" +
            "WHERE conj.id = :studentId")
    Set<KlausurEntity> findAllByStudentId(@Param("studentId")Long studentId);

}
