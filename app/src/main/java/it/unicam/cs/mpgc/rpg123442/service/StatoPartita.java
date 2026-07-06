package it.unicam.cs.mpgc.rpg123442.service;

/**
 * Lo stato complessivo di una partita.
 *
 * <p>E' un <b>enum</b> perche' gli stati possibili sono un insieme chiuso e noto:
 * una partita o e' ancora in corso, o e' stata vinta, o e' stata persa. Usare un
 * tipo dedicato (invece di due booleani "vinta"/"persa") rende impossibili gli
 * stati incoerenti, come "vinta e persa contemporaneamente".
 */
public enum StatoPartita {

    /** La partita e' ancora in corso: l'eroe e' vivo e restano nemici da sconfiggere. */
    IN_CORSO,

    /** L'eroe ha sconfitto tutti i nemici del mondo. */
    VITTORIA,

    /** L'eroe e' stato sconfitto. */
    SCONFITTA
}
