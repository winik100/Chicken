package de.hhu.propra.chicken.aggregates.student;

import de.hhu.propra.chicken.aggregates.student.KlausurReferenz;
import de.hhu.propra.chicken.aggregates.urlaub.UrlaubsEintrag;
import org.springframework.data.annotation.Id;

import java.util.Set;

public record StudentEntity(@Id Long id, String githubHandle, UrlaubsZeit restUrlaub, Set<UrlaubsEintrag> urlaube, Set<KlausurReferenz> klausurAnmeldungen) {
}
