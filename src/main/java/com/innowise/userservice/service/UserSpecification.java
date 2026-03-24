package com.innowise.userservice.service;

import com.innowise.userservice.repository.entity.User;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {

    private UserSpecification() {
    }

    public static Specification<User> containsName(String providedName) {
        return (root, query, criteriaBuilder) -> {
            if (providedName == null || providedName.isEmpty()) return null;
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("name")),
                    "%" + providedName.toLowerCase() + "%");
        };
    }

    public static Specification<User> containsSurname(String providedSurname) {
        return (root, query, criteriaBuilder) -> {
            if (providedSurname == null || providedSurname.isEmpty()) return null;
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("surname")),
                    "%" + providedSurname.toLowerCase() + "%");
        };
    }

    public static Specification<User> hasEmail(String providedEmail) {
        return (root, query, criteriaBuilder) -> {
            if (providedEmail == null || providedEmail.isEmpty()) return null;
            return criteriaBuilder.equal(criteriaBuilder.lower(root.get("email")),
                    providedEmail.toLowerCase());
        };
    }
}