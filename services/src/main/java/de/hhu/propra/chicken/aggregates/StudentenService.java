package de.hhu.propra.chicken.aggregates;

import de.hhu.propra.chicken.stereotypes.DomainService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StudentenService {

    final private StudentRepository repo;

    public StudentenService(StudentRepository repo) {
        this.repo = repo;
    }

    public void studentHinzufuegen(Long id, String githubHandle) {
        Student student = repo.studentMitId(id);
        if (student == null) {
            student = new Student(id, githubHandle);
            repo.save(student);
        }
    }

    public Student findeStudent(Long id) {
        return repo.studentMitId(id);
    }

    public Student findeStudentMitHandle(String gitHubHandle) {
        return repo.studentMitGitHubHandle(gitHubHandle);
    }
}
