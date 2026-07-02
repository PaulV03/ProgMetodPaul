package it.unicam.cs.mpgc.rpg123442.model.item;

/**
 * Astrazione di un qualsiasi oggetto di gioco (pozioni, armi, armature...).
 *
 * <p>E' il tetto comune della gerarchia degli oggetti: garantisce che ogni
 * oggetto abbia almeno un nome e una descrizione. Tipi piu' specifici
 * (consumabili, equipaggiamento...) estenderanno questa interfaccia senza
 * costringere a modificare il codice esistente (principio Open/Closed).
 */
public interface Oggetto {

    /**
     * @return il nome dell'oggetto (per l'inventario e l'interfaccia)
     */
    String getNome();

    /**
     * @return una breve descrizione di cosa fa o cos'e' l'oggetto
     */
    String getDescrizione();
}
