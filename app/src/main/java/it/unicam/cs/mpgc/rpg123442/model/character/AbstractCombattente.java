package it.unicam.cs.mpgc.rpg123442.model.character;

/**
 * Implementazione di base comune a tutti i combattenti.
 *
 * <p>Raccoglie in un solo posto lo stato e la logica condivisi da {@link Eroe},
 * {@link Nemico} (e futuri tipi): nome, statistiche, vita corrente e il modo in
 * cui si subiscono i danni. In questo modo la logica non va riscritta in ogni
 * sottoclasse (principio DRY: "Don't Repeat Yourself").
 *
 * <p>E' {@code abstract}: non ha senso creare un "combattente generico", si crea
 * sempre un tipo concreto. Il costruttore e' {@code protected} perche' deve
 * essere invocato solo dalle sottoclassi.
 */
public abstract class AbstractCombattente implements Combattente {

    private final String nome;
    private final Statistiche statistiche;
    private int vitaCorrente;

    protected AbstractCombattente(String nome, Statistiche statistiche) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Il nome non puo' essere vuoto");
        }
        if (statistiche == null) {
            throw new IllegalArgumentException("Le statistiche non possono essere null");
        }
        this.nome = nome;
        this.statistiche = statistiche;
        // Alla nascita, un combattente parte con la vita al massimo.
        this.vitaCorrente = statistiche.getVitaMassima();
    }

    @Override
    public String getNome() {
        return nome;
    }

    @Override
    public Statistiche getStatistiche() {
        return statistiche;
    }

    @Override
    public int getVitaCorrente() {
        return vitaCorrente;
    }

    @Override
    public boolean isVivo() {
        return vitaCorrente > 0;
    }

    @Override
    public void subisciDanno(int danno) {
        if (danno < 0) {
            throw new IllegalArgumentException("Il danno non puo' essere negativo");
        }
        // La vita non scende mai sotto zero: Math.max fa da "pavimento".
        this.vitaCorrente = Math.max(0, this.vitaCorrente - danno);
    }
}
