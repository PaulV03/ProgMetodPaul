package it.unicam.cs.mpgc.rpg123442.service;

import it.unicam.cs.mpgc.rpg123442.model.character.Nemico;
import it.unicam.cs.mpgc.rpg123442.model.character.Statistiche;
import it.unicam.cs.mpgc.rpg123442.model.world.Direzione;
import it.unicam.cs.mpgc.rpg123442.model.world.Mondo;
import it.unicam.cs.mpgc.rpg123442.model.world.Stanza;

/**
 * Costruisce il <b>mondo di default</b> con cui inizia una nuova partita.
 *
 * <p>La sua unica responsabilita' (SRP) e' <i>assemblare</i> la mappa: creare le
 * stanze, popolarle di nemici e collegarle fra loro. Tenere questa logica in una
 * classe dedicata (invece che dentro l'interfaccia grafica o {@code App}) mantiene
 * la UI sottile e rende la mappa facilmente testabile e sostituibile: domani si
 * potrebbe caricare il mondo da un file senza toccare il resto del gioco.
 *
 * <p>E' una classe di sole utilita': il costruttore e' privato perche' non ha
 * senso istanziarla, si usa il metodo statico {@link #creaMondoDiDefault()}.
 */
public final class MondoFactory {

    private MondoFactory() {
        // classe di utilita': non deve essere istanziata
    }

    /**
     * Crea la mappa di partenza: quattro stanze collegate a "T", con tre nemici di
     * difficolta' crescente. L'ingresso e' libero, cosi' il giocatore puo'
     * ambientarsi prima del primo scontro.
     *
     * <pre>
     *        Armeria (Scheletro)
     *           |
     *   Ingresso -- Corridoio (Goblin) -- Trono (Orco)
     * </pre>
     *
     * @return un nuovo mondo di default, pronto per una nuova partita
     */
    public static Mondo creaMondoDiDefault() {
        Stanza ingresso = new Stanza(
                "Ingresso",
                "L'atrio umido di un antico dungeon. Una torcia illumina un passaggio a est.");

        Stanza corridoio = new Stanza(
                "Corridoio",
                "Uno stretto corridoio di pietra. Qualcosa si muove nell'ombra.",
                new Nemico("Goblin", new Statistiche(20, 7, 2), 15));

        Stanza armeria = new Stanza(
                "Armeria",
                "Una sala piena di armi arrugginite, sorvegliata da un guardiano d'ossa.",
                new Nemico("Scheletro", new Statistiche(28, 9, 3), 25));

        Stanza trono = new Stanza(
                "Sala del Trono",
                "Un'ampia sala dominata da un trono di pietra e dal suo bestiale custode.",
                new Nemico("Orco", new Statistiche(40, 12, 4), 40));

        // I collegamenti sono bidirezionali: collega() crea da solo il ritorno.
        ingresso.collega(Direzione.EST, corridoio);
        corridoio.collega(Direzione.NORD, armeria);
        corridoio.collega(Direzione.EST, trono);

        Mondo mondo = new Mondo(ingresso);
        mondo.aggiungiStanza(corridoio);
        mondo.aggiungiStanza(armeria);
        mondo.aggiungiStanza(trono);
        return mondo;
    }
}
