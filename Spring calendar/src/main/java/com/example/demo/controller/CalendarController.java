package com.example.demo.controller;

import com.example.demo.dto.DayDTO;
import com.example.demo.model.Subject;
import com.example.demo.model.SubjectDate;
import com.example.demo.model.User;
import com.example.demo.repository.SubjectDateRepository;
import com.example.demo.repository.SubjectRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.CalendarService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/calendar")
public class CalendarController {

    private final CalendarService calendarService;
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final SubjectDateRepository subjectDateRepository;

    public CalendarController(CalendarService calendarService, UserRepository userRepository,
            SubjectRepository subjectRepository, SubjectDateRepository subjectDateRepository) {
        this.calendarService = calendarService;
        this.userRepository = userRepository;
        this.subjectRepository = subjectRepository;
        this.subjectDateRepository = subjectDateRepository;
    }

    @GetMapping
    public String index(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String date, // selectedDate
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        User user = null;
        if (userDetails != null) {
            user = userRepository.findByEmail(userDetails.getUsername()).orElse(null);
        }

        LocalDate now = LocalDate.now();
        int currentMonth = (month != null) ? month : now.getMonthValue();
        int currentYear = (year != null) ? year : now.getYear();

        List<DayDTO> days = calendarService.getDaysInMonth(currentYear, currentMonth, user);

        LocalDate startOfMonth = LocalDate.of(currentYear, currentMonth, 1);
        LocalDate endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth());

        List<SubjectDate> subjectDates = List.of();
        List<Subject> subjects = List.of();

        if (user != null) {
            subjectDates = subjectDateRepository.findByUserIdAndDateBetween(user.getId(), startOfMonth, endOfMonth);
            subjects = subjectRepository.findSubjectsForUserOrTutors(user.getId());
        }

        model.addAttribute("days", days);
        model.addAttribute("month", currentMonth);
        model.addAttribute("year", currentYear);
        model.addAttribute("currentDate", now);
        model.addAttribute("selectedDate", date);
        model.addAttribute("subjectDates", subjectDates);
        model.addAttribute("availableSubjects", subjects);

        return "calendar/index";
    }
}
