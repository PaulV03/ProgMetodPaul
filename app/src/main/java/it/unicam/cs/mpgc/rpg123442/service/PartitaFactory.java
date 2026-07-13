package it.unicam.cs.mpgc.rpg123442.service;

import it.unicam.cs.mpgc.rpg123442.model.character.Eroe;
import it.unicam.cs.mpgc.rpg123442.model.character.Statistiche;
import it.unicam.cs.mpgc.rpg123442.model.item.PozioneCura;

/**
 * Assembla la <b>partita iniziale</b>: l'eroe con il suo equipaggiamento, il mondo
 * da esplorare e il motore che li tiene insieme.
 *
 * <p>Perche' una classe apposta invece di due righe dentro {@code App}? Perche' i
 * valori di partenza (quanto e' forte l'eroe, quante pozioni porta con se') sono
 * <b>scelte di gioco</b>, non questioni di interfaccia grafica. Tenendole qui, la
 * UI resta sottile, la partita iniziale e' testabile, e domani si potra' partire
 * da un mondo caricato da file cambiando un metodo solo.
 *
 * <p>Come {@link MondoFactory}, e' una classe di sole utilita': non ha senso
 * istanziarla, quindi il costruttore e' privato.
 */
public final class PartitaFactory {

    /** Statistiche di partenza dell'eroe: vita, attacco, difesa. */
    private static final Statistiche STATISTICHE_INIZIALI = new Statistiche(30, 10, 4);

    /** Quante pozioni ha in tasca l'eroe all'inizio, e quanto curano. */
    private static final int POZIONI_INIZIALI = 2;
    private static final int CURA_PER_POZIONE = 12;

    private PartitaFactory() {
        // classe di utilita': non deve essere istanziata
    }

    /**
     * Crea una nuova partita: un eroe di primo livello, con qualche pozione,
     * all'ingresso del mondo di default.
     *
     * @param nomeEroe il nome scelto per il personaggio (non vuoto)
     * @return il motore di una partita pronta da giocare
     */
    public static GameEngine creaNuovaPartita(String nomeEroe) {
        Eroe eroe = new Eroe(nomeEroe, STATISTICHE_INIZIALI);
        for (int i = 0; i < POZIONI_INIZIALI; i++) {
            eroe.getInventario().aggiungi(new PozioneCura(CURA_PER_POZIONE));
        }
        return new GameEngine(eroe, MondoFactory.creaMondoDiDefault());
    }
}
