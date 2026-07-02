package it.unicam.cs.mpgc.rpg123442.model.combat;

import it.unicam.cs.mpgc.rpg123442.model.character.Combattente;

/**
 * Strategia di calcolo del danno inflitto in un attacco.
 *
 * <p>E' un'interfaccia perche' esistono molti modi possibili di calcolare il
 * danno (base, con probabilita' di colpo critico, con elementi magici...).
 * Isolando il calcolo dietro questa astrazione, il {@link Combattimento} non
 * deve cambiare quando vogliamo una nuova formula: basta fornirgli una diversa
 * implementazione. E' il pattern <b>Strategy</b> e realizza il principio
 * Open/Closed (aperto all'estensione, chiuso alla modifica).
 */
public interface CalcolatoreDanno {

    /**
     * Calcola il danno che {@code attaccante} infligge a {@code difensore}.
     *
     * @return il danno (sempre >= 0)
     */
    int calcola(Combattente attaccante, Combattente difensore);
}
