package com.tradingjournal.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Repository for managing strategy labels
 */
public class StrategyRepository {

    private static final String STRATEGIES_FILE = "strategies.json";
    private final File file;
    private final Gson gson;

    public StrategyRepository() {
        this.file = new File(STRATEGIES_FILE);
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    /**
     * Load all strategy labels
     */
    public List<String> loadAll() {
        if (!file.exists()) {
            return new ArrayList<String>();
        }

        try (FileReader reader = new FileReader(file)) {
            Type listType = new TypeToken<List<String>>() {
            }.getType();
            List<String> strategies = gson.fromJson(reader, listType);
            return strategies != null ? strategies : new ArrayList<String>();
        } catch (IOException e) {
            System.err.println("Error loading strategies: " + e.getMessage());
            return new ArrayList<String>();
        }
    }

    /**
     * Save all strategy labels
     */
    public void saveAll(List<String> strategies) {
        try (FileWriter writer = new FileWriter(file)) {
            // Sort strategies alphabetically before saving
            List<String> sortedStrategies = new ArrayList<String>(strategies);
            Collections.sort(sortedStrategies);

            gson.toJson(sortedStrategies, writer);
            System.out.println("Saved " + strategies.size() + " strategies to " + file.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error saving strategies: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Add a new strategy label
     */
    public void add(String strategy) {
        List<String> strategies = loadAll();
        if (!strategies.contains(strategy)) {
            strategies.add(strategy);
            saveAll(strategies);
        }
    }

    /**
     * Remove a strategy label
     */
    public void remove(String strategy) {
        List<String> strategies = loadAll();
        if (strategies.remove(strategy)) {
            saveAll(strategies);
        }
    }
}
