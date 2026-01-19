package com.example.demo.repository;

import com.example.demo.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long>, JpaSpecificationExecutor<Subject> {
    List<Subject> findByUserId(Long userId);

    @Query("SELECT DISTINCT s FROM Subject s JOIN s.user u LEFT JOIN u.roles r WHERE s.user.id = :userId OR r.name = 'tutor'")
    List<Subject> findSubjectsForUserOrTutors(@Param("userId") Long userId);
}
