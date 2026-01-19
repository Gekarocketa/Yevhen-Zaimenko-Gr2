package com.example.demo.repository;

import com.example.demo.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByUserIdAndDate(Long userId, LocalDate date);

    List<Note> findByUserId(Long userId);

    List<Note> findAllByDateBetween(LocalDate startDate, LocalDate endDate);
}
