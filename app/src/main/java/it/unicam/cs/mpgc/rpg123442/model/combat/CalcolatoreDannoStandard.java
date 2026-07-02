package it.unicam.cs.mpgc.rpg123442.model.combat;

import it.unicam.cs.mpgc.rpg123442.model.character.Combattente;

/**
 * Formula di danno base: {@code attacco dell'attaccante - difesa del difensore},
 * con un minimo garantito di 1 (un attacco va sempre a segno almeno un po').
 */
public class CalcolatoreDannoStandard implements CalcolatoreDanno {

    @Override
    public int calcola(Combattente attaccante, Combattente difensore) {
        int danno = attaccante.getStatistiche().getAttacco()
                - difensore.getStatistiche().getDifesa();
        return Math.max(1, danno);
    }
}
