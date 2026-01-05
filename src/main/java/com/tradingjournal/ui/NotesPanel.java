package com.tradingjournal.ui;

import com.tradingjournal.model.Note;
import com.tradingjournal.repository.NoteRepository;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Panel for managing and editing notes
 */
public class NotesPanel extends JPanel {
    private final NoteRepository noteRepository;
    private DefaultListModel<Note> noteListModel;
    private JList<Note> noteList;
    private JTextField titleField;
    private JTextArea contentArea;
    private JButton saveButton;
    private JButton deleteButton;
    private JButton newNoteButton;

    private Note currentNote;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");

    public NotesPanel(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
        initializeUI();
        loadNotes();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Left panel: List of notes
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.setPreferredSize(new Dimension(250, 0));

        JLabel notesLabel = new JLabel("My Notes");
        notesLabel.setFont(new Font("Arial", Font.BOLD, 14));
        notesLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
        leftPanel.add(notesLabel, BorderLayout.NORTH);

        noteListModel = new DefaultListModel<>();
        noteList = new JList<>(noteListModel);
        noteList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        noteList.setCellRenderer(new NoteCellRenderer());
        noteList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                onNoteSelected();
            }
        });

        JScrollPane listScrollPane = new JScrollPane(noteList);
        leftPanel.add(listScrollPane, BorderLayout.CENTER);

        newNoteButton = new JButton("New Note");
        newNoteButton.addActionListener(e -> createNewNote());
        leftPanel.add(newNoteButton, BorderLayout.SOUTH);

        add(leftPanel, BorderLayout.WEST);

        // Right panel: Note editor
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));

        // Title field
        JPanel titlePanel = new JPanel(new BorderLayout(5, 5));
        JLabel titleLabel = new JLabel("Title:");
        titleField = new JTextField();
        titleField.setFont(new Font("Arial", Font.BOLD, 14));
        titlePanel.add(titleLabel, BorderLayout.WEST);
        titlePanel.add(titleField, BorderLayout.CENTER);
        rightPanel.add(titlePanel, BorderLayout.NORTH);

        // Content area
        contentArea = new JTextArea();
        contentArea.setFont(new Font("Arial", Font.PLAIN, 13));
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        JScrollPane contentScrollPane = new JScrollPane(contentArea);
        contentScrollPane.setBorder(BorderFactory.createTitledBorder("Content"));
        rightPanel.add(contentScrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        saveButton = new JButton("Save");
        saveButton.addActionListener(e -> saveCurrentNote());
        buttonPanel.add(saveButton);

        deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> deleteCurrentNote());
        buttonPanel.add(deleteButton);

        rightPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(rightPanel, BorderLayout.CENTER);

        // Initially disable editor until a note is selected
        setEditorEnabled(false);
    }

    private void loadNotes() {
        noteListModel.clear();
        List<Note> notes = noteRepository.findAll();

        // Sort by updated date (newest first)
        notes.sort((n1, n2) -> n2.getUpdatedAt().compareTo(n1.getUpdatedAt()));

        for (Note note : notes) {
            noteListModel.addElement(note);
        }
    }

    private void onNoteSelected() {
        Note selectedNote = noteList.getSelectedValue();
        if (selectedNote != null) {
            // Save current note before switching
            if (currentNote != null && hasUnsavedChanges()) {
                saveCurrentNote();
            }

            currentNote = selectedNote;
            titleField.setText(currentNote.getTitle());
            contentArea.setText(currentNote.getContent());
            setEditorEnabled(true);
        }
    }

    private void createNewNote() {
        // Save current note if there are changes
        if (currentNote != null && hasUnsavedChanges()) {
            saveCurrentNote();
        }

        Note newNote = new Note("New Note", "");
        noteRepository.save(newNote);
        loadNotes();

        // Select the new note
        for (int i = 0; i < noteListModel.size(); i++) {
            if (noteListModel.get(i).getId().equals(newNote.getId())) {
                noteList.setSelectedIndex(i);
                break;
            }
        }

        titleField.requestFocus();
        titleField.selectAll();
    }

    private void saveCurrentNote() {
        if (currentNote != null) {
            currentNote.setTitle(titleField.getText().trim());
            currentNote.setContent(contentArea.getText());
            noteRepository.save(currentNote);
            loadNotes();

            // Reselect the current note
            for (int i = 0; i < noteListModel.size(); i++) {
                if (noteListModel.get(i).getId().equals(currentNote.getId())) {
                    noteList.setSelectedIndex(i);
                    break;
                }
            }

            JOptionPane.showMessageDialog(this, "Note saved successfully!",
                    "Saved", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void deleteCurrentNote() {
        if (currentNote != null) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete this note?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                noteRepository.delete(currentNote.getId());
                currentNote = null;
                titleField.setText("");
                contentArea.setText("");
                setEditorEnabled(false);
                loadNotes();
            }
        }
    }

    private boolean hasUnsavedChanges() {
        if (currentNote == null)
            return false;

        String currentTitle = titleField.getText().trim();
        String currentContent = contentArea.getText();

        return !currentTitle.equals(currentNote.getTitle()) ||
                !currentContent.equals(currentNote.getContent());
    }

    private void setEditorEnabled(boolean enabled) {
        titleField.setEnabled(enabled);
        contentArea.setEnabled(enabled);
        saveButton.setEnabled(enabled);
        deleteButton.setEnabled(enabled);
    }

    /**
     * Custom cell renderer for note list
     */
    private class NoteCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {

            JLabel label = (JLabel) super.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);

            if (value instanceof Note) {
                Note note = (Note) value;
                String title = note.getTitle();
                if (title == null || title.trim().isEmpty()) {
                    title = "Untitled Note";
                }
                String dateStr = note.getUpdatedAt().format(DATE_FORMATTER);

                label.setText("<html><b>" + title + "</b><br/>" +
                        "<small style='color: gray;'>" + dateStr + "</small></html>");
                label.setBorder(new EmptyBorder(5, 5, 5, 5));
            }

            return label;
        }
    }
}
