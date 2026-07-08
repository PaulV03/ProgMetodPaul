package it.unicam.cs.mpgc.rpg123442.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import it.unicam.cs.mpgc.rpg123442.repository.dto.PartitaDTO;
import it.unicam.cs.mpgc.rpg123442.service.GameEngine;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Implementazione JSON del {@link GameSaveRepository}, basata sulla libreria Gson.
 *
 * <p>Il formato JSON e' stato scelto perche' semplice e <b>leggibile a occhio</b>:
 * aprendo il file di salvataggio si vede l'intero mondo come elenco di stanze con
 * le loro uscite (nodi e archi del grafo). La classe traduce la partita in un
 * {@link PartitaDTO} tramite il {@link PartitaMapper} e delega a Gson la sola
 * scrittura/lettura del testo JSON.
 *
 * <p>Essendo dietro l'interfaccia {@link GameSaveRepository}, domani la si puo'
 * affiancare o sostituire con un'implementazione XML o su database senza toccare
 * il resto del programma.
 */
public class JsonGameSaveRepository implements GameSaveRepository {

    /** {@code setPrettyPrinting} rende il file indentato e leggibile. */
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public void salva(GameEngine partita, Path percorso) {
        if (partita == null || percorso == null) {
            throw new IllegalArgumentException("Partita e percorso non possono essere null");
        }
        PartitaDTO dto = PartitaMapper.toDTO(partita);
        try (Writer writer = Files.newBufferedWriter(percorso)) {
            gson.toJson(dto, writer);
        } catch (IOException e) {
            throw new PersistenzaException("Impossibile salvare la partita in: " + percorso, e);
        }
    }

    @Override
    public GameEngine carica(Path percorso) {
        if (percorso == null) {
            throw new IllegalArgumentException("Il percorso non puo' essere null");
        }
        try (Reader reader = Files.newBufferedReader(percorso)) {
            PartitaDTO dto = gson.fromJson(reader, PartitaDTO.class);
            if (dto == null) {
                throw new PersistenzaException("Il file di salvataggio e' vuoto: " + percorso);
            }
            return PartitaMapper.fromDTO(dto);
        } catch (IOException e) {
            throw new PersistenzaException("Impossibile caricare la partita da: " + percorso, e);
        } catch (JsonParseException e) {
            throw new PersistenzaException("Il file di salvataggio non e' un JSON valido: " + percorso, e);
        }
    }
}
