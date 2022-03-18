package de.hhu.propra.chicken.aggregates;

import de.hhu.propra.chicken.aggregates.UrlaubsEintrag;
import de.hhu.propra.chicken.util.KlausurReferenz;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Set;

//TODO: feldernamen an spring data anpassen (evtl. @MappedCollection)
@Table("student")
public record StudentEntity(@Id Long id, String githubHandle, Long restUrlaub, Set<Long> klausurReferenzen) {
}
