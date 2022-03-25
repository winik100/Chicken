package de.hhu.propra.chicken.aggregates;

public interface StudentRepository {

    Student studentMitId(Long id);

    void save(Student student);

    Student studentMitGitHubHandle(String gitHubHandle);
}
