package com.innowise.userservice.service;

import com.innowise.userservice.repository.entity.User;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {

    public static Specification<User> containsName(String providedName) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("name")),
                        "%" + providedName.toLowerCase() + "%");
    }

    public static Specification<User> containsSurname(String providedSurname) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("surname")),
                        "%" + providedSurname.toLowerCase() + "%");
    }
}
