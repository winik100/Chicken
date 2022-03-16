package de.hhu.propra.chicken.aggregates;

import de.hhu.propra.chicken.stereotypes.DomainService;

import java.util.Optional;

@DomainService
public class StudentenService {

    final private StudentRepository repo;

    public StudentenService(StudentRepository repo) {
        this.repo = repo;
    }

    void studentHinzufuegen(Long id, String githubHandle) {
        Student student = repo.studentMitId(id);
        if (student == null) {
            student = new Student(id, githubHandle);
            repo.save(student);
        }
    }

    Optional<Student> findeStudent(Long id) {
        Student student = repo.studentMitId(id);
        return Optional.ofNullable(student);
    }


}
