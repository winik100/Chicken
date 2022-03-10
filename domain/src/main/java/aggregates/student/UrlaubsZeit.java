package aggregates.student;

class UrlaubsZeit {

    private Long minuten;

    public UrlaubsZeit() {
        this.minuten = 240L;
    }

    boolean zeitHinzufuegen(Long minuten) {
        Long zwischenmin = this.minuten + minuten;
        if (zwischenmin <= 240L) {
            this.minuten = zwischenmin;
            return true;
        }
        return false;
    }

    boolean zeitEntfernen(Long minuten) {
        Long zwischenmin = this.minuten - minuten;
        if (zwischenmin >= 0L) {
            this.minuten = zwischenmin;
            return true;
        }
        return false;
    }

    public Long getMinuten() {
        return minuten;
    }
}
