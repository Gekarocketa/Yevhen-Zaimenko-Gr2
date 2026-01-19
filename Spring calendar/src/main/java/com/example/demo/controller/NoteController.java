package com.example.demo.controller;

import com.example.demo.model.Note;
import com.example.demo.model.User;
import com.example.demo.repository.NoteRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.CalendarService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequestMapping("/notes")
public class NoteController {

    private final CalendarService calendarService;
    private final UserRepository userRepository;
    private final NoteRepository noteRepository;

    public NoteController(CalendarService calendarService, UserRepository userRepository,
            NoteRepository noteRepository) {
        this.calendarService = calendarService;
        this.userRepository = userRepository;
        this.noteRepository = noteRepository;
    }

    @PostMapping
    public String store(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam String content,
            @RequestParam Integer priority,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        calendarService.addNote(user, date, content, priority);
        return "redirect:/calendar?year=" + date.getYear() + "&month=" + date.getMonthValue() + "&date=" + date;
    }

    @PutMapping("/{id}")
    public String update(
            @PathVariable Long id,
            @RequestParam String content,
            @RequestParam Integer priority,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        calendarService.updateNote(id, user, content, priority);
        // Redirect to specific date if provided, else just calendar
        if (date != null) {
            return "redirect:/calendar?year=" + date.getYear() + "&month=" + date.getMonthValue() + "&date=" + date;
        }
        Note note = noteRepository.findById(id).orElseThrow();
        return "redirect:/calendar?date=" + note.getDate();
    }

    @DeleteMapping("/{id}")
    public String destroy(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        Note note = noteRepository.findById(id).orElseThrow();
        LocalDate date = note.getDate();
        calendarService.deleteNote(id, user);
        return "redirect:/calendar?year=" + date.getYear() + "&month=" + date.getMonthValue() + "&date=" + date;
    }
}
