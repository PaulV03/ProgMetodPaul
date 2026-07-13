package it.unicam.cs.mpgc.rpg123442.ui;

/**
 * Le schermate fra cui il gioco puo' spostarsi.
 *
 * <p>Serve a tenere i controller <b>indipendenti dall'applicazione</b>: quando lo
 * scontro finisce, il controller del combattimento non deve sapere chi mostrera'
 * la schermata successiva ne' come (uno {@code Stage} JavaFX? una finestra
 * diversa? un test?). Chiede solo di andare da un'altra parte, e chi implementa
 * questa interfaccia decide il come.
 *
 * <p>E' il principio di inversione delle dipendenze (DIP): i controller dipendono
 * da questa astrazione, non da {@code App}. In cambio si guadagna anche la
 * testabilita', perche' nei test se ne puo' passare una versione finta che si
 * limita ad annotare dove le e' stato chiesto di andare.
 */
public interface Navigazione {

    /**
     * Mostra lo schermo di esplorazione, con l'eroe nella stanza in cui si trova.
     */
    void mostraEsplorazione();

    /**
     * Apre lo scontro con il nemico della stanza corrente.
     */
    void mostraCombattimento();
}
