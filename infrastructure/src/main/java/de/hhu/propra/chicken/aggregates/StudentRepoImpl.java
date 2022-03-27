package de.hhu.propra.chicken.aggregates;

import de.hhu.propra.chicken.util.KlausurReferenz;
import de.hhu.propra.chicken.util.UrlaubsEintragDTO;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class StudentRepoImpl implements StudentRepository {

    DBStudentRepo studentRepo;
    DBKlausurRepo klausurRepo;

    public StudentRepoImpl(DBStudentRepo studentRepo, DBKlausurRepo klausurRepo) {
        this.studentRepo = studentRepo;
        this.klausurRepo = klausurRepo;
    }

    @Override
    public void save(Student student) {
        StudentEntity studentEntity = new StudentEntity(student.getId(),
                student.getGithubHandle(),
                student.getResturlaubInMin(),
                student.getKlausurAnmeldungen().stream()
                        .map(KlausurReferenz::new)
                        .collect(Collectors.toSet()),
                student.getUrlaubeAlsDTOs().stream()
                        .map(x -> new UrlaubsEintragEntity(x.start(), x.ende()))
                        .collect(Collectors.toSet()));
        studentRepo.save(studentEntity);
    }

    @Override
    public Student studentMitGitHubHandle(String gitHubHandle) {
        Optional<StudentEntity> studentEntity = studentRepo.findByGithubHandle(gitHubHandle);
        StudentEntity student = studentEntity.orElse(null);
        if (student == null) {
            return null;
        }
        Set<UrlaubsEintragEntity> urlaubsEintragEntities = student.urlaubsEintraege();
        Set<UrlaubsEintragDTO> urlaubsEintragDTOs = urlaubsEintragEntities.stream()
                .map(x -> new UrlaubsEintragDTO(x.start(), x.ende()))
                .collect(Collectors.toSet());
        Set<KlausurEntity> klausurEntities = klausurRepo.findAllByStudentId(student.id());
        Set<KlausurReferenz> ids = klausurEntities.stream()
                .map(x -> new KlausurReferenz(x.getId()))
                .collect(Collectors.toSet());
        return new Student(student.id(), student.githubHandle(), student.restUrlaub(), urlaubsEintragDTOs, ids);
    }
}
