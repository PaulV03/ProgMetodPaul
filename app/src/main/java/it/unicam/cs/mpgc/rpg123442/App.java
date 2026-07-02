package it.unicam.cs.mpgc.rpg123442;

import it.unicam.cs.mpgc.rpg123442.model.character.Eroe;
import it.unicam.cs.mpgc.rpg123442.model.character.Nemico;
import it.unicam.cs.mpgc.rpg123442.model.character.Statistiche;
import it.unicam.cs.mpgc.rpg123442.model.combat.Combattimento;
import it.unicam.cs.mpgc.rpg123442.model.combat.RisultatoTurno;
import it.unicam.cs.mpgc.rpg123442.model.item.PozioneCura;

/**
 * Punto di ingresso dell'applicazione.
 *
 * <p>Per ora contiene una piccola DEMO di combattimento, utile solo a vedere il
 * dominio in azione. Verra' sostituita dall'interfaccia vera (prima console,
 * poi JavaFX) piu' avanti.
 */
public class App {

    public static void main(String[] args) {
        Eroe eroe = new Eroe("Aragorn", new Statistiche(30, 10, 4));
        Nemico goblin = new Nemico("Goblin", new Statistiche(20, 7, 2), 15);

        // L'eroe porta con se' una pozione di cura.
        PozioneCura pozione = new PozioneCura(10);
        eroe.getInventario().aggiungi(pozione);

        System.out.println("Inizia lo scontro: " + eroe.getNome() + " vs " + goblin.getNome());
        System.out.println("--------------------------------------------------");

        Combattimento combattimento = new Combattimento(eroe, goblin);
        while (!combattimento.isFinito()) {
            RisultatoTurno r = combattimento.eseguiTurno();
            System.out.printf("%s attacca %s per %d danni  ->  vita di %s: %d%n",
                    r.attaccante().getNome(),
                    r.difensore().getNome(),
                    r.danno(),
                    r.difensore().getNome(),
                    r.difensore().getVitaCorrente());
        }

        System.out.println("--------------------------------------------------");
        combattimento.getVincitore()
                .ifPresent(v -> System.out.println("Vincitore: " + v.getNome()));

        // Se l'eroe sopravvive, guadagna l'esperienza rilasciata dal nemico.
        if (eroe.isVivo()) {
            eroe.guadagnaEsperienza(goblin.getEsperienzaRilasciata());
            System.out.printf("%s guadagna %d XP  ->  livello %d (%d/%d al prossimo livello)%n",
                    eroe.getNome(),
                    goblin.getEsperienzaRilasciata(),
                    eroe.getLivello(),
                    eroe.getEsperienza(),
                    eroe.esperienzaProssimoLivello());

            System.out.printf("Vita di %s dopo lo scontro: %d/%d%n",
                    eroe.getNome(), eroe.getVitaCorrente(), eroe.getStatistiche().getVitaMassima());

            // L'eroe usa la pozione dall'inventario per rimettersi in forze.
            if (eroe.getInventario().contiene(pozione)) {
                pozione.usaSu(eroe);
                eroe.getInventario().rimuovi(pozione);
                System.out.printf("%s beve una %s  ->  vita %d/%d (oggetti rimasti: %d)%n",
                        eroe.getNome(), pozione.getNome(),
                        eroe.getVitaCorrente(), eroe.getStatistiche().getVitaMassima(),
                        eroe.getInventario().dimensione());
            }
        }
    }
}
