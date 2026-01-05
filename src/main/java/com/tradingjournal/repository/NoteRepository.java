package com.tradingjournal.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.tradingjournal.model.Note;
import com.tradingjournal.util.LocalDateTimeAdapter;

import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repository for managing notes persistence
 */
public class NoteRepository {
    private static final String NOTES_FILE = "notes.json";
    private final Gson gson;

    public NoteRepository() {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .setPrettyPrinting()
                .create();
    }

    public List<Note> findAll() {
        File file = new File(NOTES_FILE);
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (Reader reader = new FileReader(file)) {
            Type listType = new TypeToken<ArrayList<Note>>() {
            }.getType();
            List<Note> notes = gson.fromJson(reader, listType);
            return notes != null ? notes : new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void save(Note note) {
        List<Note> notes = findAll();

        // Update existing or add new
        Optional<Note> existing = notes.stream()
                .filter(n -> n.getId().equals(note.getId()))
                .findFirst();

        if (existing.isPresent()) {
            notes.remove(existing.get());
        }

        notes.add(note);
        saveAll(notes);
    }

    public void delete(String id) {
        List<Note> notes = findAll();
        notes.removeIf(n -> n.getId().equals(id));
        saveAll(notes);
    }

    public Optional<Note> findById(String id) {
        return findAll().stream()
                .filter(n -> n.getId().equals(id))
                .findFirst();
    }

    private void saveAll(List<Note> notes) {
        try (Writer writer = new FileWriter(NOTES_FILE)) {
            gson.toJson(notes, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
