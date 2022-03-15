package de.hhu.propra.chicken.aggregates.student;

import org.springframework.data.repository.CrudRepository;

public interface DBStudentRepo extends CrudRepository<StudentEntity, Long> {

}
