package com.example.demo.service;

import com.example.demo.model.Subject;
import com.example.demo.model.User;
import com.example.demo.repository.SubjectDateRepository;
import com.example.demo.repository.SubjectRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final SubjectDateRepository subjectDateRepository;

    public SubjectService(SubjectRepository subjectRepository, SubjectDateRepository subjectDateRepository) {
        this.subjectRepository = subjectRepository;
        this.subjectDateRepository = subjectDateRepository;
    }

    // ... searchSubjects and createSubject methods remain unchanged ...
    @Transactional(readOnly = true)
    public Page<Subject> searchSubjects(String search, String semester, String owner, Pageable pageable) {
        return subjectRepository.findAll((Specification<Subject>) (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (search != null && !search.isEmpty()) {
                predicates.add(cb.like(root.get("name"), "%" + search + "%"));
            }
            if (semester != null && !semester.isEmpty()) {
                predicates.add(cb.equal(root.get("semester"), semester));
            }
            if (owner != null && !owner.isEmpty()) {
                predicates.add(cb.like(root.get("user").get("name"), "%" + owner + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable);
    }

    @Transactional
    public void createSubject(User user, String name, String color, String description, String semester) {
        Subject subject = new Subject();
        subject.setUser(user);
        subject.setName(name);
        subject.setColor(color != null ? color : "#4eae6e");
        subject.setDescription(description);
        subject.setSemester(semester);
        subject.setCreatedBy(user);
        subject.setUpdatedBy(user);

        subjectRepository.save(subject);
    }

    @Transactional
    public void updateSubject(Long id, User user, String name, String color, String description, String semester) {
        Subject subject = subjectRepository.findById(id).orElseThrow(() -> new RuntimeException("Subject not found"));

        if (!subject.getUser().getId().equals(user.getId()) &&
                !user.hasRole("admin") && !user.hasRole("tutor")) {
            throw new RuntimeException("Unauthorized");
        }

        subject.setName(name);
        subject.setColor(color);
        subject.setDescription(description);
        subject.setSemester(semester);
        subject.setUpdatedBy(user);
        subjectRepository.save(subject);
    }

    @Transactional
    public void deleteSubject(Long id, User user) {
        Subject subject = subjectRepository.findById(id).orElseThrow(() -> new RuntimeException("Subject not found"));
        if (!subject.getUser().getId().equals(user.getId()) &&
                !user.hasRole("admin") && !user.hasRole("tutor")) {
            throw new RuntimeException("Unauthorized");
        }
        // Delete all schedule entries for this subject first
        subjectDateRepository.deleteBySubjectId(id);

        subjectRepository.delete(subject);
    }

    @Transactional(readOnly = true)
    public List<Subject> getUserSubjects(User user) {
        return subjectRepository.findByUserId(user.getId());
    }
}
