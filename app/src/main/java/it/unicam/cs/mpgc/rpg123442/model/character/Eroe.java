package it.unicam.cs.mpgc.rpg123442.model.character;

/**
 * Il personaggio controllato dal giocatore.
 *
 * <p>Per ora si comporta come un combattente base. In seguito qui aggiungeremo
 * le caratteristiche tipiche dell'eroe (esperienza, livello, inventario...):
 * tenerlo in una classe propria ci permette di far crescere solo l'eroe senza
 * toccare {@link Nemico}.
 */
public class Eroe extends AbstractCombattente {

    public Eroe(String nome, Statistiche statistiche) {
        super(nome, statistiche);
    }
}
