package com.example.demo.service;

import com.example.demo.dto.DayDTO;
import com.example.demo.model.Note;
import com.example.demo.model.SubjectDate;
import com.example.demo.model.User;
import com.example.demo.repository.NoteRepository;
import com.example.demo.repository.SubjectDateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CalendarService {

    private final NoteRepository noteRepository;
    private final SubjectDateRepository subjectDateRepository;

    public CalendarService(NoteRepository noteRepository, SubjectDateRepository subjectDateRepository) {
        this.noteRepository = noteRepository;
        this.subjectDateRepository = subjectDateRepository;
    }

    @Transactional(readOnly = true)
    public List<DayDTO> getDaysInMonth(int year, int month, User currentUser) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.plusMonths(1).minusDays(1);

        List<Note> allNotes = noteRepository.findAllByDateBetween(start, end);

        Map<LocalDate, List<Note>> notesByDate = allNotes.stream()
                .filter(note -> {
                    boolean isTutorNote = note.getUser().hasRole("tutor");
                    if (currentUser == null) {
                        return isTutorNote;
                    } else {
                        return note.getUser().getId().equals(currentUser.getId()) || isTutorNote;
                    }
                })
                .collect(Collectors.groupingBy(Note::getDate));

        List<DayDTO> days = new ArrayList<>();

        // Add empty days for alignment (Mon=1, ... Sun=7)
        int dayOfWeek = start.getDayOfWeek().getValue(); // 1..7
        for (int i = 1; i < dayOfWeek; i++) {
            days.add(null);
        }

        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            List<Note> dailyNotes = notesByDate.getOrDefault(date, new ArrayList<>());

            Note tutorNote = dailyNotes.stream()
                    .filter(n -> n.getUser().hasRole("tutor"))
                    .findFirst().orElse(null);

            Note displayNote = (tutorNote != null) ? tutorNote : dailyNotes.stream().findFirst().orElse(null);

            DayDTO dayDTO = new DayDTO();
            dayDTO.setDate(date);
            dayDTO.setNoteContent(displayNote != null ? displayNote.getContent() : null);
            dayDTO.setPriority(displayNote != null ? displayNote.getPriority() : null);
            dayDTO.setTutor(tutorNote != null);
            dayDTO.setNotes(dailyNotes); // Add all notes for the day
            days.add(dayDTO);
        }
        return days;
    }

    @Transactional(readOnly = true)
    public List<SubjectDate> getSubjectDates(int year, int month, User user) {
        if (user == null)
            return new ArrayList<>();
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.plusMonths(1).minusDays(1);
        return subjectDateRepository.findByUserIdAndDateBetween(user.getId(), start, end);
    }

    @Transactional(readOnly = true)
    public Map<LocalDate, List<Note>> getUserNotesWithContent(int year, int month, User user) {
        if (user == null)
            return Map.of();
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.plusMonths(1).minusDays(1);

        return noteRepository.findAllByDateBetween(start, end).stream()
                .filter(n -> n.getUser().getId().equals(user.getId()))
                .collect(Collectors.groupingBy(Note::getDate));
    }

    @Transactional(readOnly = true)
    public List<Note> getDayNotes(LocalDate date, User currentUser) {
        if (date == null)
            return new ArrayList<>();
        List<Note> allNotes = noteRepository.findAllByDateBetween(date, date);

        return allNotes.stream()
                .filter(note -> {
                    boolean isTutorNote = note.getUser().hasRole("tutor");
                    if (currentUser == null) {
                        return isTutorNote;
                    } else {
                        return note.getUser().getId().equals(currentUser.getId()) || isTutorNote;
                    }
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void addNote(User user, LocalDate date, String content, Integer priority) {
        Note note = new Note();
        note.setUser(user);
        note.setDate(date);
        note.setContent(content);
        note.setPriority(priority);
        // RODO
        note.setCreatedBy(user);
        note.setUpdatedBy(user);

        noteRepository.save(note);
    }

    @Transactional
    public void updateNote(Long noteId, User user, String content, Integer priority) {
        Note note = noteRepository.findById(noteId).orElseThrow(() -> new RuntimeException("Note not found"));
        if (!note.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }
        note.setContent(content);
        note.setPriority(priority);
        // RODO
        note.setUpdatedBy(user);
        noteRepository.save(note);
    }

    @Transactional
    public void deleteNote(Long noteId, User user) {
        Note note = noteRepository.findById(noteId).orElseThrow(() -> new RuntimeException("Note not found"));
        if (!note.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }
        noteRepository.delete(note);
    }
}
