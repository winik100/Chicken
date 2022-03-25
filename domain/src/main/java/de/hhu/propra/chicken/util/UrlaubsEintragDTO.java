package de.hhu.propra.chicken.util;

import java.time.Duration;
import java.time.LocalDateTime;

public record UrlaubsEintragDTO(LocalDateTime start, LocalDateTime ende) {

    public Long dauerInMin() {
        return Duration.between(start, ende).toMinutes();
    }
}
