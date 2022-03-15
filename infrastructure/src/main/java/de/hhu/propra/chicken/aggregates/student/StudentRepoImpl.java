package de.hhu.propra.chicken.aggregates.student;

import de.hhu.propra.chicken.repositories.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public class StudentRepoImpl implements StudentRepository {

    DBStudentRepo repo;

    public StudentRepoImpl(DBStudentRepo repo) {
        this.repo = repo;
    }

    @Override
    public Student studentMitId(Long id) {
        Optional<StudentEntity> studentEntity = repo.findById(id);
        StudentEntity student = studentEntity.orElse(null);
        if(student == null) {
            return null;
        }
        return new Student(student.id(), student.githubHandle());
    }

    @Override
    public void save(Student student) {
        StudentEntity studentEntity = new StudentEntity(student.getId(), student.getGithubHandle(), student.getResturlaubInMin(), student.getUrlaube(), student.getKlausurAnmeldungen());
        repo.save(studentEntity);
    }
}
