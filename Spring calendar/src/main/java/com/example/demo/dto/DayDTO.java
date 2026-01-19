package com.example.demo.dto;

import java.time.LocalDate;

public class DayDTO {
    private LocalDate date;
    private String noteContent; // Main note content for the cell
    private Integer priority;
    private boolean isTutor;
    private java.util.List<com.example.demo.model.Note> notes = new java.util.ArrayList<>();

    public DayDTO() {
    }

    public DayDTO(LocalDate date, String noteContent, Integer priority, boolean isTutor,
            java.util.List<com.example.demo.model.Note> notes) {
        this.date = date;
        this.noteContent = noteContent;
        this.priority = priority;
        this.isTutor = isTutor;
        this.notes = notes;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getNoteContent() {
        return noteContent;
    }

    public void setNoteContent(String noteContent) {
        this.noteContent = noteContent;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public boolean isTutor() {
        return isTutor;
    }

    public void setTutor(boolean tutor) {
        isTutor = tutor;
    }

    public java.util.List<com.example.demo.model.Note> getNotes() {
        return notes;
    }

    public void setNotes(java.util.List<com.example.demo.model.Note> notes) {
        this.notes = notes;
    }
}
