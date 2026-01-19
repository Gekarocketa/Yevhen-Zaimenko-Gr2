package com.example.demo.repository;

import com.example.demo.model.SubjectDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SubjectDateRepository extends JpaRepository<SubjectDate, Long> {
    List<SubjectDate> findByUserIdAndDate(Long userId, LocalDate date);

    List<SubjectDate> findByUserId(Long userId);

    List<SubjectDate> findByUserIdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate);

    void deleteBySubjectId(Long subjectId);
}
