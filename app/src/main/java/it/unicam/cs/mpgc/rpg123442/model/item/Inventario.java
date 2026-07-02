package it.unicam.cs.mpgc.rpg123442.model.item;

import java.util.ArrayList;
import java.util.List;

/**
 * Contenitore degli oggetti posseduti da un personaggio.
 *
 * <p>La sua unica responsabilita' (SRP) e' custodire una lista di {@link Oggetto}
 * e offrire operazioni di base per gestirla. Non decide <i>come</i> gli oggetti
 * vengano usati: quella logica appartiene al motore di gioco.
 */
public class Inventario {

    private final List<Oggetto> oggetti = new ArrayList<>();

    /**
     * Aggiunge un oggetto all'inventario.
     */
    public void aggiungi(Oggetto oggetto) {
        if (oggetto == null) {
            throw new IllegalArgumentException("Non si puo' aggiungere un oggetto null");
        }
        oggetti.add(oggetto);
    }

    /**
     * Rimuove un oggetto dall'inventario.
     *
     * @return true se l'oggetto era presente ed e' stato rimosso
     */
    public boolean rimuovi(Oggetto oggetto) {
        return oggetti.remove(oggetto);
    }

    /**
     * @return true se l'inventario contiene l'oggetto indicato
     */
    public boolean contiene(Oggetto oggetto) {
        return oggetti.contains(oggetto);
    }

    /**
     * @return il numero di oggetti presenti
     */
    public int dimensione() {
        return oggetti.size();
    }

    /**
     * @return true se l'inventario e' vuoto
     */
    public boolean isVuoto() {
        return oggetti.isEmpty();
    }

    /**
     * @return una copia non modificabile della lista degli oggetti.
     *         Restituire una copia protegge lo stato interno: chi la riceve non
     *         puo' alterare l'inventario aggiungendo o togliendo elementi.
     */
    public List<Oggetto> getOggetti() {
        return List.copyOf(oggetti);
    }
}
