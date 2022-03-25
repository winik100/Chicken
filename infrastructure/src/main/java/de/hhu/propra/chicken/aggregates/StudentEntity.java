package de.hhu.propra.chicken.aggregates;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Set;

@Table("student")
public record StudentEntity(@Id Long id,
                            @Column("github_handle")String githubHandle,
                            @Column("rest_urlaub")Long restUrlaub,
                            @MappedCollection(idColumn = "id") Set<KlausurReferenz> klausurReferenzen,
                            @MappedCollection(idColumn = "student_id") Set<UrlaubsEintragEntity> urlaubsEintraege) {
}
