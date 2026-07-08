package it.unicam.cs.mpgc.rpg123442.repository;

import it.unicam.cs.mpgc.rpg123442.service.GameEngine;

import java.nio.file.Path;

/**
 * Contratto per salvare e caricare una partita su una memoria persistente.
 *
 * <p>E' un'<b>astrazione</b> volutamente indipendente dal formato: chi la usa
 * (l'interfaccia di gioco) sa soltanto che puo' <i>salvare</i> e <i>caricare</i>
 * una partita, non se dietro ci sia JSON, XML o un database. Cosi' possiamo
 * cambiare tecnologia di persistenza (o affiancarne di nuove) senza toccare il
 * resto del programma: e' il principio di inversione delle dipendenze (DIP) e
 * l'Open/Closed di SOLID.
 *
 * <p>La prima implementazione e' {@link JsonGameSaveRepository} (formato JSON).
 */
public interface GameSaveRepository {

    /**
     * Salva lo stato completo di una partita nel percorso indicato,
     * sovrascrivendo un eventuale salvataggio precedente.
     *
     * @param partita  la partita da salvare (non null)
     * @param percorso il file su cui scrivere (non null)
     * @throws PersistenzaException se il salvataggio non va a buon fine
     */
    void salva(GameEngine partita, Path percorso);

    /**
     * Ricostruisce una partita a partire da un salvataggio.
     *
     * @param percorso il file da cui leggere (non null, deve esistere)
     * @return la partita ripristinata, pronta per essere ripresa
     * @throws PersistenzaException se il caricamento non va a buon fine o il file
     *                              non contiene un salvataggio valido
     */
    GameEngine carica(Path percorso);
}
