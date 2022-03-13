package de.hhu.propra.chicken.repositories;

import de.hhu.propra.chicken.aggregates.student.Student;

public interface StudentRepository {
    Student studentMitId(Long id);

    void save(Student student);
}
