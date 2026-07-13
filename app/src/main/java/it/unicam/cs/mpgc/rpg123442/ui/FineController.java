package it.unicam.cs.mpgc.rpg123442.ui;

import it.unicam.cs.mpgc.rpg123442.model.character.Eroe;
import it.unicam.cs.mpgc.rpg123442.service.GameEngine;
import it.unicam.cs.mpgc.rpg123442.service.StatoPartita;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Controller dello schermo di fine partita (<code>fine.fxml</code>).
 *
 * <p>E' l'ultima parola sulla partita: dice com'e' andata e offre l'unica cosa che
 * abbia ancora senso fare, cioe' tornare al menu. Come gli altri controller non
 * contiene regole di gioco: <b>chi</b> abbia vinto lo stabilisce il motore con
 * {@link GameEngine#statoPartita()}, qui si decide soltanto come raccontarlo.
 *
 * <p>Il costruttore rifiuta una partita ancora in corso: una schermata di fine
 * partita per una partita che non e' finita sarebbe una contraddizione, e vale la
 * pena scoprirla subito invece di mostrare un esito senza senso.
 */
public class FineController {

    private final GameEngine partita;
    private final Navigazione navigazione;

    @FXML private Label titoloFine;
    @FXML private Label esito;
    @FXML private Label riepilogo;

    /**
     * @param partita     la partita conclusa da raccontare (non null e non in corso)
     * @param navigazione a chi chiedere il ritorno al menu (non null)
     * @throws IllegalArgumentException se la partita e' ancora {@link StatoPartita#IN_CORSO}
     */
    public FineController(GameEngine partita, Navigazione navigazione) {
        if (partita == null || navigazione == null) {
            throw new IllegalArgumentException("Partita e navigazione non possono essere null");
        }
        if (partita.statoPartita() == StatoPartita.IN_CORSO) {
            throw new IllegalArgumentException("La partita non e' ancora finita");
        }
        this.partita = partita;
        this.navigazione = navigazione;
    }

    /**
     * Chiamato da JavaFX a iniezione avvenuta: e' qui che la schermata prende forma.
     */
    @FXML
    private void initialize() {
        boolean vittoria = partita.statoPartita() == StatoPartita.VITTORIA;

        titoloFine.setText(vittoria ? "Vittoria" : "Sconfitta");
        esito.setText(vittoria
                ? "Il dungeon e' ripulito: nessun nemico resta in piedi."
                : "L'eroe cade, e il dungeon torna al silenzio.");
        StileEsito.applica(esito, vittoria);

        riepilogo.setText(descrivi(partita.getEroe()));
    }

    /**
     * Riporta il giocatore al menu iniziale, da dove potra' cominciarne un'altra.
     */
    @FXML
    private void tornaAlMenu() {
        navigazione.mostraMenuIniziale();
    }

    /** L'epitaffio dell'eroe: fin dove e' arrivato prima che la partita chiudesse. */
    private String descrivi(Eroe eroe) {
        return eroe.getNome()
                + " - livello " + eroe.getLivello()
                + " - esperienza " + eroe.getEsperienza();
    }
}
