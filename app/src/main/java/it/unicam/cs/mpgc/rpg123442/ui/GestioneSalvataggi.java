package it.unicam.cs.mpgc.rpg123442.ui;

/**
 * Le due cose che il giocatore puo' chiedere sui salvataggi: mettere da parte la
 * partita, o riprenderne una.
 *
 * <p>E' un'interfaccia separata da {@link Navigazione} anche se le implementa la
 * stessa classe. La ragione e' il principio di segregazione delle interfacce (ISP):
 * lo schermo di combattimento ha bisogno di cambiare schermata ma non di salvare, e
 * non deve trovarsi fra le mani metodi che non lo riguardano. Chi vede meno, puo'
 * rompere meno.
 *
 * <p>Come per la navigazione, i controller dicono <i>che cosa</i> vogliono e non
 * <i>come</i> ottenerlo: dove finisca il file, quale libreria scriva il JSON, che
 * cosa mostrare se il disco e' pieno sono decisioni di chi implementa. Il controller
 * resta cosi' privo di dialoghi da aprire, e quindi verificabile nei test.
 */
public interface GestioneSalvataggi {

    /**
     * Mette da parte la partita in corso, chiedendo al giocatore dove.
     */
    void salvaPartita();

    /**
     * Riprende una partita salvata in precedenza, chiedendo al giocatore quale.
     *
     * <p>La partita eventualmente in corso viene abbandonata: e' il giocatore ad
     * averlo chiesto.
     */
    void caricaPartita();
}
