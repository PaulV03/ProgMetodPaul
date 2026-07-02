package it.unicam.cs.mpgc.rpg123442.model.combat;

import it.unicam.cs.mpgc.rpg123442.model.character.Combattente;

import java.util.Optional;

/**
 * Gestisce uno scontro a turni fra due {@link Combattente}.
 *
 * <p>Ad ogni turno l'attaccante di turno colpisce l'avversario; poi il turno
 * passa all'altro. Il combattimento finisce quando uno dei due non e' piu' vivo.
 *
 * <p>Dipende solo dalle astrazioni {@link Combattente} e {@link CalcolatoreDanno}:
 * non sa se combattono un eroe e un goblin, ne' come e' fatta la formula del
 * danno. Questo lo rende riutilizzabile ed estendibile (principi DIP e OCP).
 */
public class Combattimento {

    private final Combattente sfidante1;
    private final Combattente sfidante2;
    private final CalcolatoreDanno calcolatoreDanno;
    private Combattente turnoDi;

    /**
     * Crea un combattimento specificando la strategia di calcolo del danno.
     */
    public Combattimento(Combattente sfidante1, Combattente sfidante2, CalcolatoreDanno calcolatoreDanno) {
        if (sfidante1 == null || sfidante2 == null) {
            throw new IllegalArgumentException("I combattenti non possono essere null");
        }
        if (sfidante1 == sfidante2) {
            throw new IllegalArgumentException("Un combattente non puo' scontrarsi con se stesso");
        }
        if (calcolatoreDanno == null) {
            throw new IllegalArgumentException("Il calcolatore di danno non puo' essere null");
        }
        this.sfidante1 = sfidante1;
        this.sfidante2 = sfidante2;
        this.calcolatoreDanno = calcolatoreDanno;
        this.turnoDi = sfidante1; // per ora inizia sempre il primo sfidante
    }

    /**
     * Crea un combattimento con la formula di danno standard.
     */
    public Combattimento(Combattente sfidante1, Combattente sfidante2) {
        this(sfidante1, sfidante2, new CalcolatoreDannoStandard());
    }

    /**
     * @return true se il combattimento e' terminato (uno dei due e' sconfitto)
     */
    public boolean isFinito() {
        return !sfidante1.isVivo() || !sfidante2.isVivo();
    }

    /**
     * Esegue un turno: l'attaccante di turno colpisce l'avversario.
     *
     * @return il resoconto del turno appena giocato
     * @throws IllegalStateException se il combattimento e' gia' finito
     */
    public RisultatoTurno eseguiTurno() {
        if (isFinito()) {
            throw new IllegalStateException("Il combattimento e' gia' finito");
        }
        Combattente attaccante = turnoDi;
        Combattente difensore = (turnoDi == sfidante1) ? sfidante2 : sfidante1;

        int danno = calcolatoreDanno.calcola(attaccante, difensore);
        difensore.subisciDanno(danno);

        turnoDi = difensore; // il turno passa all'avversario
        return new RisultatoTurno(attaccante, difensore, danno);
    }

    /**
     * @return il vincitore, se il combattimento e' finito; altrimenti vuoto.
     *         Usiamo {@link Optional} per evitare di restituire {@code null}.
     */
    public Optional<Combattente> getVincitore() {
        if (!isFinito()) {
            return Optional.empty();
        }
        return Optional.of(sfidante1.isVivo() ? sfidante1 : sfidante2);
    }
}
