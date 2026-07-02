package it.unicam.cs.mpgc.rpg123442.model.combat;

import it.unicam.cs.mpgc.rpg123442.model.character.Combattente;

/**
 * Descrive cosa e' accaduto in un singolo turno di combattimento:
 * chi ha attaccato, chi ha subito e quanti danni.
 *
 * <p>E' un {@code record}: un modo conciso di Java per creare una classe
 * <b>immutabile</b> che serve solo a trasportare dati. Il compilatore genera da
 * solo costruttore, getter ({@code attaccante()}, {@code difensore()}, {@code danno()}),
 * {@code equals}, {@code hashCode} e {@code toString}.
 *
 * <p>Questo oggetto permette al combattimento (dominio) di "raccontare" cosa e'
 * successo senza sapere nulla di come verra' mostrato: la console o la GUI
 * leggeranno questi dati e li presenteranno come preferiscono.
 */
public record RisultatoTurno(Combattente attaccante, Combattente difensore, int danno) {

    /**
     * @return true se, dopo questo turno, il difensore e' stato sconfitto
     */
    public boolean difensoreSconfitto() {
        return !difensore.isVivo();
    }
}
