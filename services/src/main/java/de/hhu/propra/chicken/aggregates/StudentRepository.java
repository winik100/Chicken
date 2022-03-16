package de.hhu.propra.chicken.aggregates;

import de.hhu.propra.chicken.aggregates.Student;

public interface StudentRepository {
    Student studentMitId(Long id);

    void save(Student student);
}
