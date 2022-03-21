package de.hhu.propra.chicken.aggregates;

import de.hhu.propra.chicken.util.KlausurReferenz;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Set;

//TODO: feldernamen an spring data anpassen (evtl. @MappedCollection)
@Table("student")
public record StudentEntity(@Id Long id,
                            @Column("github_handle")String githubHandle,
                            @Column("rest_urlaub")Long restUrlaub,
                            @MappedCollection(idColumn = "id") Set<KlausurReferenz> klausurReferenzen,
                            @MappedCollection(idColumn = "id") Set<UrlaubsEintragEntity> urlaubsEintraege) {
}
