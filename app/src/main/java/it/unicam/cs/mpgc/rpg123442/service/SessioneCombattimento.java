package it.unicam.cs.mpgc.rpg123442.service;

import it.unicam.cs.mpgc.rpg123442.model.character.Eroe;
import it.unicam.cs.mpgc.rpg123442.model.character.Nemico;
import it.unicam.cs.mpgc.rpg123442.model.combat.Combattimento;
import it.unicam.cs.mpgc.rpg123442.model.combat.RisultatoTurno;
import it.unicam.cs.mpgc.rpg123442.model.item.Consumabile;
import it.unicam.cs.mpgc.rpg123442.model.world.Stanza;

import java.util.ArrayList;
import java.util.List;

/**
 * Uno scontro <b>in corso</b> fra l'eroe e il nemico di una stanza, giocato
 * un'azione alla volta.
 *
 * <p>Sta in {@code service} e non nel dominio perche' la sua responsabilita' e'
 * di <b>regia</b>: il {@link Combattimento} sa solo far alternare i turni e
 * infliggere danni, mentre le <i>conseguenze sulla partita</i> (l'esperienza che
 * l'eroe guadagna, la stanza che resta libera dopo la vittoria) non sono affari
 * suoi. Qui invece si tengono insieme le due cose.
 *
 * <p>Il giocatore compie <b>una</b> azione per volta ({@link #attacca()} oppure
 * {@link #usaConsumabile(Consumabile)}); ad ogni azione il nemico risponde
 * subito, se e' ancora in piedi. Ogni azione restituisce il resoconto dei turni
 * giocati, cosi' l'interfaccia grafica puo' raccontarli senza sapere nulla delle
 * regole.
 *
 * <p>Si ottiene solo da {@link GameEngine#iniziaCombattimento()}: il costruttore
 * non e' pubblico proprio per evitare che nasca uno scontro scollegato dalla
 * partita a cui deve applicare i suoi effetti.
 */
public class SessioneCombattimento {

    private final Eroe eroe;
    private final Nemico nemico;
    private final Stanza stanza;
    private final Combattimento scontro;

    /**
     * Apre lo scontro fra l'eroe e il nemico che sorveglia la stanza indicata.
     * L'eroe muove per primo.
     *
     * @param eroe   il personaggio del giocatore (non null)
     * @param stanza la stanza in cui avviene lo scontro (non null)
     * @throws IllegalStateException se nella stanza non c'e' alcun nemico
     */
    SessioneCombattimento(Eroe eroe, Stanza stanza) {
        if (eroe == null || stanza == null) {
            throw new IllegalArgumentException("Eroe e stanza non possono essere null");
        }
        this.eroe = eroe;
        this.stanza = stanza;
        this.nemico = stanza.getNemico().orElseThrow(
                () -> new IllegalStateException("Non c'e' nessun nemico da combattere in questa stanza"));
        this.scontro = new Combattimento(eroe, nemico); // l'eroe attacca per primo
    }

    /**
     * @return il personaggio del giocatore
     */
    public Eroe getEroe() {
        return eroe;
    }

    /**
     * @return l'avversario di questo scontro
     */
    public Nemico getNemico() {
        return nemico;
    }

    /**
     * @return true se lo scontro e' concluso: uno dei due e' caduto
     */
    public boolean isFinito() {
        return scontro.isFinito();
    }

    /**
     * @return true se lo scontro e' finito con l'eroe ancora in piedi
     */
    public boolean eroeVincitore() {
        return isFinito() && eroe.isVivo();
    }

    /**
     * L'eroe attacca; se il nemico sopravvive, risponde subito.
     *
     * @return i turni giocati, in ordine: il colpo dell'eroe e, se c'e' stata,
     *         la risposta del nemico
     * @throws IllegalStateException se lo scontro e' gia' finito
     */
    public List<RisultatoTurno> attacca() {
        verificaInCorso();
        List<RisultatoTurno> turni = new ArrayList<>();
        turni.add(scontro.eseguiTurno());
        aggiungiRispostaDelNemico(turni);
        concludiSeFinito();
        return List.copyOf(turni);
    }

    /**
     * L'eroe usa un consumabile invece di attaccare: l'oggetto produce il suo
     * effetto, si esaurisce, e il turno passa al nemico che ne approfitta per
     * colpire.
     *
     * <p>Il parametro e' l'astrazione {@link Consumabile} e non la pozione di cura:
     * cosi' un nuovo tipo di consumabile funziona qui senza che questa classe venga
     * modificata (Open/Closed).
     *
     * @param consumabile l'oggetto da usare, che deve trovarsi nell'inventario dell'eroe
     * @return i turni giocati: la sola risposta del nemico, se c'e' stata
     * @throws IllegalStateException    se lo scontro e' gia' finito
     * @throws IllegalArgumentException se l'oggetto e' null o l'eroe non lo possiede
     */
    public List<RisultatoTurno> usaConsumabile(Consumabile consumabile) {
        verificaInCorso();
        if (consumabile == null) {
            throw new IllegalArgumentException("Il consumabile non puo' essere null");
        }
        if (!eroe.getInventario().contiene(consumabile)) {
            throw new IllegalArgumentException("L'eroe non possiede: " + consumabile.getNome());
        }

        consumabile.usaSu(eroe);
        eroe.getInventario().rimuovi(consumabile); // un consumabile si esaurisce con l'uso
        scontro.passaTurno();                      // l'azione e' costata il turno

        List<RisultatoTurno> turni = new ArrayList<>();
        aggiungiRispostaDelNemico(turni);
        concludiSeFinito();
        return List.copyOf(turni);
    }

    /**
     * Fa colpire il nemico, ma solo se lo scontro non si e' gia' chiuso con la
     * mossa dell'eroe: un nemico appena caduto non risponde.
     */
    private void aggiungiRispostaDelNemico(List<RisultatoTurno> turni) {
        if (!scontro.isFinito()) {
            turni.add(scontro.eseguiTurno());
        }
    }

    /**
     * Applica alla partita le conseguenze di una vittoria: l'eroe incassa
     * l'esperienza del nemico e la stanza resta sgombra.
     *
     * <p>Se l'eroe cade, invece, il nemico resta al suo posto: una partita
     * caricata da un salvataggio precedente lo ritrovera' li'.
     */
    private void concludiSeFinito() {
        if (eroeVincitore()) {
            eroe.guadagnaEsperienza(nemico.getEsperienzaRilasciata());
            stanza.rimuoviNemico();
        }
    }

    /**
     * Ogni azione presuppone uno scontro ancora aperto: agire su uno scontro
     * concluso e' un errore di chi lo comanda (l'interfaccia deve chiedere prima
     * {@link #isFinito()}), non una mossa di gioco.
     */
    private void verificaInCorso() {
        if (isFinito()) {
            throw new IllegalStateException("Lo scontro e' gia' finito");
        }
    }
}
