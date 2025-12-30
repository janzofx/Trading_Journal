package com.tradingjournal.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.tradingjournal.model.Account;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Repository for managing accounts
 */
public class AccountRepository {

    private static final String ACCOUNTS_FILE = "accounts.json";
    private final File file;
    private final Gson gson;

    public AccountRepository() {
        this.file = new File(ACCOUNTS_FILE);
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    /**
     * Load all accounts
     */
    public List<Account> loadAll() {
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (FileReader reader = new FileReader(file)) {
            Type listType = new TypeToken<List<Account>>() {
            }.getType();
            List<Account> accounts = gson.fromJson(reader, listType);
            return accounts != null ? accounts : new ArrayList<>();
        } catch (IOException e) {
            System.err.println("Error loading accounts: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Find account by name
     */
    public Optional<Account> findByName(String name) {
        if (name == null)
            return Optional.empty();
        return loadAll().stream()
                .filter(a -> name.equalsIgnoreCase(a.getName()))
                .findFirst();
    }

    /**
     * Save all accounts
     */
    public void saveAll(List<Account> accounts) {
        try (FileWriter writer = new FileWriter(file)) {
            // Sort accounts alphabetically
            List<Account> sortedAccounts = new ArrayList<>(accounts);
            Collections.sort(sortedAccounts);

            gson.toJson(sortedAccounts, writer);
        } catch (IOException e) {
            System.err.println("Error saving accounts: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Add a new account
     */
    public void add(Account account) {
        List<Account> accounts = loadAll();
        // Remove existing if present (update)
        accounts.removeIf(a -> a.getName().equalsIgnoreCase(account.getName()));
        accounts.add(account);
        saveAll(accounts);
    }

    /**
     * Remove an account
     */
    public void remove(String name) {
        List<Account> accounts = loadAll();
        if (accounts.removeIf(a -> a.getName().equalsIgnoreCase(name))) {
            saveAll(accounts);
        }
    }

    /**
     * Rename an account
     */
    public boolean rename(String oldName, String newName) {
        if (oldName == null || newName == null || oldName.trim().isEmpty() || newName.trim().isEmpty()) {
            return false;
        }

        List<Account> accounts = loadAll();

        // Check if new name already exists (and it's not the same account)
        if (accounts.stream()
                .anyMatch(a -> a.getName().equalsIgnoreCase(newName) && !a.getName().equalsIgnoreCase(oldName))) {
            return false; // New name already exists
        }

        // Find and rename the account
        Optional<Account> accountToRename = accounts.stream()
                .filter(a -> a.getName().equalsIgnoreCase(oldName))
                .findFirst();

        if (accountToRename.isPresent()) {
            Account account = accountToRename.get();
            account.setName(newName);
            saveAll(accounts);
            return true;
        }

        return false;
    }
}
