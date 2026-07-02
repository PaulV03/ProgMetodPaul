package it.unicam.cs.mpgc.rpg123442.model.character;

/**
 * Il personaggio controllato dal giocatore.
 *
 * <p>Oltre a essere un combattente, l'eroe ha una <b>progressione</b>: accumula
 * punti esperienza (sconfiggendo nemici) e, al superamento di una soglia, sale
 * di livello diventando piu' forte.
 */
public class Eroe extends AbstractCombattente {

    /** Bonus di statistiche guadagnati a ogni passaggio di livello. */
    private static final int BONUS_VITA_PER_LIVELLO = 5;
    private static final int BONUS_ATTACCO_PER_LIVELLO = 2;
    private static final int BONUS_DIFESA_PER_LIVELLO = 1;

    private int livello;
    private int esperienza;

    public Eroe(String nome, Statistiche statistiche) {
        super(nome, statistiche);
        this.livello = 1;
        this.esperienza = 0;
    }

    public int getLivello() {
        return livello;
    }

    public int getEsperienza() {
        return esperienza;
    }

    /**
     * @return l'esperienza necessaria per passare dal livello attuale al successivo.
     *         Curva semplice: 100 punti per livello (livello 1 -> 100, livello 2 -> 200, ...).
     */
    public int esperienzaProssimoLivello() {
        return livello * 100;
    }

    /**
     * Fa guadagnare esperienza all'eroe. Se si supera la soglia del livello
     * (anche piu' volte di seguito), l'eroe sale di livello di conseguenza.
     *
     * @param quantita punti esperienza guadagnati (non negativi)
     */
    public void guadagnaEsperienza(int quantita) {
        if (quantita < 0) {
            throw new IllegalArgumentException("L'esperienza guadagnata non puo' essere negativa");
        }
        this.esperienza += quantita;
        while (this.esperienza >= esperienzaProssimoLivello()) {
            this.esperienza -= esperienzaProssimoLivello();
            salaDiLivello();
        }
    }

    /**
     * Aumenta il livello e potenzia le statistiche, curando l'eroe a vita piena.
     */
    private void salaDiLivello() {
        this.livello++;
        Statistiche attuali = getStatistiche();
        Statistiche potenziate = new Statistiche(
                attuali.getVitaMassima() + BONUS_VITA_PER_LIVELLO,
                attuali.getAttacco() + BONUS_ATTACCO_PER_LIVELLO,
                attuali.getDifesa() + BONUS_DIFESA_PER_LIVELLO
        );
        setStatistiche(potenziate);
        ripristinaVitaAlMassimo();
    }
}
