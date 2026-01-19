package com.example.demo.service;

import com.example.demo.model.AdminActionLog;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.AdminActionLogRepository;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AdminActionLogRepository logRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminService(UserRepository userRepository, RoleRepository roleRepository,
            AdminActionLogRepository logRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.logRepository = logRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public Page<User> searchUsers(String search, String roleName, Pageable pageable) {
        Specification<User> spec = (root, query, cb) -> {
            if (search != null && !search.isEmpty()) {
                return cb.or(
                        cb.like(root.get("name"), "%" + search + "%"),
                        cb.like(root.get("email"), "%" + search + "%"));
            }
            return null;
        };

        if (roleName != null && !roleName.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.join("roles").get("name"), roleName));
        }

        return userRepository.findAll(spec, pageable);
    }

    @Transactional(readOnly = true)
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Transactional
    public void createUser(User admin, String name, String email, String password, List<Long> roleIds) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email taken");
        }

        Set<Role> roles = roleIds.stream()
                .map(id -> roleRepository.findById(id).orElseThrow())
                .collect(Collectors.toSet());

        if (roles.isEmpty()) {
            roles.add(roleRepository.findByName("user").orElseThrow());
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(roles);
        user.setCreatedBy(admin);
        user.setUpdatedBy(admin);

        User saved = userRepository.save(user);

        logAction(admin, "user_created", saved.getId(), "Created user " + email);
    }

    @Transactional
    public void updateUser(Long userId, User admin, String name, String email, String password, List<Long> roleIds) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setName(name);
        user.setEmail(email);
        user.setUpdatedBy(admin);

        if (password != null && !password.isEmpty()) {
            user.setPassword(passwordEncoder.encode(password));
        }

        if (roleIds != null) {
            Set<Role> roles = roleIds.stream()
                    .map(id -> roleRepository.findById(id).orElseThrow())
                    .collect(Collectors.toSet());
            user.setRoles(roles);
        }

        userRepository.save(user);
        logAction(admin, "user_updated", user.getId(), "Updated user " + email);
    }

    @Transactional
    public void deleteUser(Long userId, User admin) {
        User user = userRepository.findById(userId).orElseThrow();
        userRepository.delete(user);
        logAction(admin, "user_deleted", userId, "Deleted user " + user.getEmail());
    }

    @Transactional
    public void requirePasswordReset(Long userId, User admin) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setPasswordResetRequired(true);
        userRepository.save(user);
        logAction(admin, "password_reset_required", userId, "Reset required for " + user.getEmail());
    }

    private void logAction(User admin, String action, Long targetId, String details) {
        AdminActionLog log = new AdminActionLog();
        log.setUserId(admin.getId());
        log.setAction(action);
        log.setTargetUserId(targetId);
        log.setDetails(details);

        logRepository.save(log);
    }
}
