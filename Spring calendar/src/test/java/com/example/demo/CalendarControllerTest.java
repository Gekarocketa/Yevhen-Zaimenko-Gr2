package com.example.demo;

import com.example.demo.controller.CalendarController;
import com.example.demo.model.User;
import com.example.demo.repository.SubjectDateRepository;
import com.example.demo.repository.SubjectRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.CalendarService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(CalendarController.class)
public class CalendarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CalendarService calendarService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private SubjectRepository subjectRepository;

    @MockBean
    private SubjectDateRepository subjectDateRepository;

    // NoteRepository is NOT injected in Controller based on view_file, but verify
    // if it's used in Security or other config loaded by WebMvcTest?
    // WebMvcTest might load GlobalMethodSecurityComponents which might need
    // UserDetailsService.
    // I should probably mock CustomUserDetailsService if it's scanned.
    // But @WebMvcTest usually only loads the controller.
    // However, SecurityConfig is likely picked up. SecurityConfig uses
    // CustomUserDetailsService.
    // So I need to mock CustomUserDetailsService.

    @MockBean
    private com.example.demo.service.CustomUserDetailsService customUserDetailsService;

    @Test
    @WithMockUser(username = "test@example.com")
    public void testIndex() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        mockMvc.perform(get("/calendar"))
                .andExpect(status().isOk())
                .andExpect(view().name("calendar/index"));
    }
}
