package com.example.demo.controller;

import com.example.demo.model.Subject;
import com.example.demo.model.SubjectDate;
import com.example.demo.model.User;
import com.example.demo.repository.SubjectDateRepository;
import com.example.demo.repository.SubjectRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
public class SubjectDateController {

    private final SubjectDateRepository subjectDateRepository;
    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;

    public SubjectDateController(SubjectDateRepository subjectDateRepository, SubjectRepository subjectRepository,
            UserRepository userRepository) {
        this.subjectDateRepository = subjectDateRepository;
        this.subjectRepository = subjectRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/add-subject-to-day")
    @ResponseBody
    public String addSubjectToDay(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam Long subjectId,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        Subject subject = subjectRepository.findById(subjectId).orElseThrow();

        // Check if already exists
        List<SubjectDate> existing = subjectDateRepository.findByUserIdAndDate(user.getId(), date);
        boolean alreadyExists = existing.stream().anyMatch(sd -> sd.getSubject().getId().equals(subjectId));

        if (!alreadyExists) {
            SubjectDate sd = new SubjectDate();
            sd.setUser(user);
            sd.setSubject(subject);
            sd.setDate(date);
            // RODO
            sd.setCreatedBy(user);
            sd.setUpdatedBy(user);
            subjectDateRepository.save(sd);
        }

        return "ok";
    }

    @PutMapping("/subject-date/{id}/move")
    @ResponseBody
    public String move(@PathVariable Long id, @RequestParam String time,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        SubjectDate sd = subjectDateRepository.findById(id).orElseThrow();

        if (!sd.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        sd.setStartTime(java.time.LocalTime.parse(time));
        sd.setUpdatedBy(user);
        subjectDateRepository.save(sd);
        return "ok";
    }

    @DeleteMapping("/subject-date/{id}")
    public String destroy(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        SubjectDate sd = subjectDateRepository.findById(id).orElseThrow();

        if (!sd.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        subjectDateRepository.delete(sd);
        return "redirect:/calendar"; // Return to calendar after delete
    }
}
