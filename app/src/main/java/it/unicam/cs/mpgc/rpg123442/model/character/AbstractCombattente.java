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
    private Statistiche statistiche;
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

    @Override
    public void curati(int quantita) {
        if (quantita < 0) {
            throw new IllegalArgumentException("La cura non puo' essere negativa");
        }
        if (!isVivo()) {
            return; // un combattente sconfitto non si cura
        }
        // La vita non supera mai il massimo: Math.min fa da "tetto".
        this.vitaCorrente = Math.min(statistiche.getVitaMassima(), this.vitaCorrente + quantita);
    }

    /**
     * Sostituisce le statistiche del combattente.
     *
     * <p>E' {@code protected}: solo le sottoclassi possono usarlo, ed e' pensato
     * per i combattenti che fanno <b>evolvere</b> le proprie statistiche nel
     * tempo (per esempio l'{@link Eroe} che sale di livello). I combattenti
     * "normali" non lo usano e le loro statistiche restano di fatto costanti.
     */
    protected void setStatistiche(Statistiche statistiche) {
        if (statistiche == null) {
            throw new IllegalArgumentException("Le statistiche non possono essere null");
        }
        this.statistiche = statistiche;
    }

    /**
     * Riporta la vita corrente al massimo consentito dalle statistiche attuali.
     */
    protected void ripristinaVitaAlMassimo() {
        this.vitaCorrente = statistiche.getVitaMassima();
    }
}
