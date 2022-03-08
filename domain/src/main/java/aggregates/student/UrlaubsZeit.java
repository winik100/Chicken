package aggregates.student;

class UrlaubsZeit {

    private Long minuten;

    public UrlaubsZeit() {
        this.minuten = 240L;
    }

    void zeitHinzufuegen(Long minuten) {
        Long zwischenmin = this.minuten + minuten;
        if(zwischenmin<=240L) {
            this.minuten = zwischenmin;
        }
    }

    void zeitEntfernen(Long minuten) {
        Long zwischenmin = this.minuten - minuten;
        if(zwischenmin>=0L) {
            this.minuten = zwischenmin;
        }
    }

    public Long getMinuten() {
        return minuten;
    }
}
