package repositories;

import aggregates.student.Student;

public interface StudentRepository {
    Student studentMitId(Long id);

    void save(Student student);
}
