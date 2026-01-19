package com.example.demo.controller;

import com.example.demo.model.Subject;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.SubjectService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/subjects")
public class SubjectController {

    private final SubjectService subjectService;
    private final UserRepository userRepository;

    public SubjectController(SubjectService subjectService, UserRepository userRepository) {
        this.subjectService = subjectService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String index(
            // Wyszukiwanie i filtrowanie (Поиск и фильтрация)
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String semester,
            @RequestParam(required = false) String owner,
            // Stronicowanie (Пагинация)
            @PageableDefault(size = 9) Pageable pageable,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();

        boolean isTutorOrAdmin = user.hasRole("tutor") || user.hasRole("admin");

        if (isTutorOrAdmin) {
            Page<Subject> subjects = subjectService.searchSubjects(search, semester, owner, pageable);
            model.addAttribute("subjects", subjects);
            model.addAttribute("isTutor", true);
        } else {
            model.addAttribute("subjects", subjectService.getUserSubjects(user));
            model.addAttribute("isTutor", false);
        }

        return "subjects/index";
    }

    @PostMapping
    public String store(
            @RequestParam String name,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String semester,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();

        if (!user.hasRole("tutor") && !user.hasRole("admin")) {
            return "redirect:/subjects?error=unauthorized";
        }

        subjectService.createSubject(user, name, color, description, semester);
        return "redirect:/subjects";
    }

    @PutMapping("/{id}")
    public String update(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String semester,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        subjectService.updateSubject(id, user, name, color, description, semester);
        return "redirect:/subjects";
    }

    @DeleteMapping("/{id}")
    public String destroy(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        subjectService.deleteSubject(id, user);
        return "redirect:/subjects";
    }
}
