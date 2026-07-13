# ProgMetodPaul — RPG a combattimento a turni

Progetto d'esame per il corso di **Metodologie di Programmazione** (A.A. 2025/26, Università di Camerino).

Un piccolo gioco di ruolo: si esplora un dungeon stanza per stanza, si affrontano i nemici
che lo sorvegliano in **combattimenti a turni**, si accumula esperienza e si sale di livello.
La partita si vince ripulendo il dungeon, si perde cadendo in battaglia.

Il gioco è il **contesto**, non il fine: l'obiettivo del progetto è un sistema software
modulare, estendibile e testato, non un RPG ricco di funzionalità.

- **Autore:** Paul Vaida — matricola 123442
- **Package radice:** `it.unicam.cs.mpgc.rpg123442`
- **Documentazione estesa:** vedi la [Wiki](../../wiki) del repository

## Requisiti

- **JDK 25** (il progetto dichiara una toolchain Java 25; Gradle la scarica se non è presente)
- Nessuna installazione di Gradle: si usa il **wrapper** incluso (`gradlew`)
- Nessuna installazione di JavaFX: la scarica il plugin `org.openjfx.javafxplugin`

## Come si esegue

Dalla cartella del progetto:

```bash
./gradlew run      # avvia il gioco (finestra JavaFX)
./gradlew build    # compila ed esegue tutti i test
./gradlew test     # solo i test
```

Su Windows, da PowerShell o cmd, i comandi sono `.\gradlew.bat run` e così via.

## Come si gioca

1. **Nuova Partita** dal menu iniziale — oppure **Carica Partita**, per riprendere
   un'avventura interrotta.
2. Ci si sposta fra le stanze con i pulsanti **Nord / Sud / Est / Ovest**: sono attivi solo
   dove esiste davvero un'uscita.
3. Dove una stanza è sorvegliata, si accende **Combatti!**.
4. Nello scontro si sceglie un'azione per volta: **Attacca** (colpisci, e il nemico risponde)
   oppure **Usa pozione** (recuperi vita, ma cedi il turno). Il diario racconta ogni colpo.
5. Vinto lo scontro, l'eroe incassa l'esperienza e la stanza resta libera.
6. Con **Salva** e **Carica** la partita si mette da parte e si riprende in qualsiasi momento.
7. La partita finisce quando l'ultimo nemico cade (**vittoria**) o quando cade l'eroe
   (**sconfitta**): in entrambi i casi si arriva alla schermata di fine, che riepiloga
   fin dove è arrivato l'eroe.

## Struttura del progetto

Tutto il codice sta sotto `it.unicam.cs.mpgc.rpg123442`, diviso per **responsabilità**:

| Package | Responsabilità |
|---|---|
| `model.character` | Chi combatte: `Combattente` (interfaccia), `Statistiche`, `AbstractCombattente`, `Eroe`, `Nemico` |
| `model.combat` | Come si combatte: `Combattimento` (turni), `CalcolatoreDanno` (strategia di calcolo) + implementazione standard, `RisultatoTurno` |
| `model.item` | Cosa si porta con sé: `Oggetto`, `Consumabile`, `PozioneCura`, `Inventario` |
| `model.world` | Dove si combatte: `Stanza`, `Direzione`, `Mondo` (grafo di stanze) |
| `service` | Le regole della partita: `GameEngine`, `SessioneCombattimento`, `StatoPartita`, le factory del mondo e della partita |
| `repository` | Salvataggio e caricamento: `GameSaveRepository` (contratto) e la sua implementazione JSON |
| `ui` | L'interfaccia JavaFX: i controller, le schermate `.fxml`, il foglio di stile, e i contratti `Navigazione` e `GestioneSalvataggi` con cui i controller chiedono qualcosa senza sapere chi gliela darà |
| `App` | Punto d'ingresso: assembla la partita, mostra le schermate e realizza quei due contratti |

La dipendenza va **in una direzione sola**: `ui` e `repository` conoscono il `service`,
il `service` conosce il `model`, il `model` non conosce nessuno. Il dominio del gioco può
quindi essere riusato con un'interfaccia diversa (console, web, mobile) senza modifiche.

## Persistenza

Il salvataggio è in **JSON** (libreria Gson), dietro l'interfaccia `GameSaveRepository`
(`salva` / `carica`): il resto del programma non sa in che formato si stia scrivendo, quindi
un'implementazione XML o su database prenderebbe il posto di quella JSON senza toccare
nient'altro.

Il mondo è un **grafo** di stanze (le uscite sono archi fra i nodi), e un grafo con cicli non
si può serializzare direttamente. Viene perciò appiattito in un formato a **nodi + archi per
nome** (`PartitaDTO` e i suoi record annidati) e ricostruito al caricamento da `PartitaMapper`.

Dal gioco si salva e si carica con gli omonimi pulsanti, ma i controller **non sanno come**:
chiedono soltanto "salva" all'interfaccia `GestioneSalvataggi`, e chi la implementa (`App`)
sceglie il file e chiama il repository. È anche ciò che rende i due pulsanti verificabili nei
test, dove al loro posto si passa una gestione finta che non apre nessuna finestra di dialogo.

## Test

Il progetto ha **113 test JUnit 5**, eseguiti da `./gradlew build`. Coprono il dominio
(statistiche, combattimento, progressione, inventario, mondo), le regole di partita
(`GameEngine`, `SessioneCombattimento`), la persistenza (andata e ritorno su file temporaneo)
e l'interfaccia: i test della UI **caricano davvero i file FXML e premono i pulsanti**, perché
un `fx:id` sbagliato o un `onAction` che punta a un metodo inesistente non sono errori di
compilazione e si scoprono solo così.

## Stato del progetto

Il gioco è completo e giocabile dall'inizio alla fine: esplorazione del dungeon,
combattimento a turni con uso di oggetti, progressione dell'eroe, salvataggio e
caricamento su file, vittoria e sconfitta con la relativa schermata di fine partita.

Restano fuori, per scelta, le funzionalità che avrebbero fatto crescere il gioco senza
aggiungere nulla al progetto software: più classi di eroe, equipaggiamento, nemici che si
muovono. Il codice è predisposto per accoglierle — un nuovo consumabile o una nuova
formula di danno non richiedono di modificare le classi esistenti — ma il gioco resta il
contesto, non il fine.

## Dichiarazione sull'uso di strumenti di Intelligenza Artificiale

Per la realizzazione di questo progetto ho utilizzato un assistente basato su Intelligenza Artificiale (Claude, tramite Claude Code) come strumento di supporto durante lo sviluppo.

L'IA è stata utilizzata principalmente per:

- confrontare e discutere le scelte progettuali (organizzazione dei package, responsabilità delle classi e applicazione dei principi SOLID);
- chiarire dubbi progettuali e valutare possibili alternative implementative;
- ricevere suggerimenti su possibili soluzioni e sulla struttura dei test.

Le decisioni progettuali, l'implementazione del progetto, le modifiche al codice e la verifica del corretto funzionamento sono state svolte e controllate personalmente. Ogni soluzione adottata è stata compresa e verificata prima di essere inserita nel progetto, e sono in grado di motivare e spiegare le scelte implementative effettuate.
