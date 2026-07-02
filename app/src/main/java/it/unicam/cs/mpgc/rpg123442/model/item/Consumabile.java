package it.unicam.cs.mpgc.rpg123442.model.item;

import it.unicam.cs.mpgc.rpg123442.model.character.Combattente;

/**
 * Un {@link Oggetto} che puo' essere usato su un combattente, producendo un
 * effetto immediato (per esempio una pozione che cura).
 *
 * <p>Tipicamente un consumabile si esaurisce dopo l'uso: sara' compito di chi
 * gestisce l'inventario rimuoverlo dopo averlo usato.
 */
public interface Consumabile extends Oggetto {

    /**
     * Applica l'effetto del consumabile al combattente indicato.
     *
     * @param bersaglio il combattente su cui usare l'oggetto
     */
    void usaSu(Combattente bersaglio);
}
