package com.vish.pms.specification;

import org.springframework.data.jpa.domain.Specification;

import com.vish.pms.entity.User;

public class UserSpecification {
    public static Specification<User> hasName(String name) {
        return (root, query, cb) -> {
            if (name == null)
                return cb.conjunction();
            return cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
        };
    }

    public static Specification<User> hasEmail(String email) {
        return (root, query, cb) -> {
            if (email == null)
                return cb.conjunction();
            return cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%");
        };
    }

    public static Specification<User> hasRole(String role) {
        return (root, query, cb) -> {
            if (role == null)
                return cb.conjunction();
            return cb.like(cb.lower(root.get("role")), "%" + role.toLowerCase() + "%");
        };
    }

}
