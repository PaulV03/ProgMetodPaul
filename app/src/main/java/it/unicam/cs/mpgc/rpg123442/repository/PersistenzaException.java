package it.unicam.cs.mpgc.rpg123442.repository;

/**
 * Segnala che un'operazione di salvataggio o caricamento e' fallita.
 *
 * <p>Serve a <b>nascondere i dettagli tecnici</b> della persistenza (per esempio
 * un {@link java.io.IOException} o un errore di formato JSON) dietro un unico tipo
 * di errore di dominio: chi usa il {@link GameSaveRepository} gestisce "la partita
 * non si e' potuta salvare/caricare" senza dover conoscere la tecnologia usata
 * sotto. La causa originale resta comunque disponibile via {@link #getCause()}.
 *
 * <p>E' una {@link RuntimeException} (non controllata) perche' un fallimento di
 * I/O non e' normalmente recuperabile nel mezzo del gioco: si preferisce lasciarla
 * emergere con un messaggio chiaro piuttosto che costringere ogni chiamante a
 * gestirla.
 */
public class PersistenzaException extends RuntimeException {

    public PersistenzaException(String messaggio) {
        super(messaggio);
    }

    public PersistenzaException(String messaggio, Throwable causa) {
        super(messaggio, causa);
    }
}
