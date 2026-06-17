package com.hnq.repository.specification;

import com.hnq.model.User;
import com.hnq.util.Gender;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.domain.Specification;
// test
public class UserSpec {

    public static Specification<User> hasFirstName(String firstName) {
        return new Specification<User>() {
            @Override
            public Predicate toPredicate(@NonNull Root<User> root, CriteriaQuery<?> query, @NonNull CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.like(root.get("firstName"), "%" + firstName + "%");
            }
        };
    }

    public static Specification<User> notEqualGender(Gender gender) {
        return new Specification<User>() {
            @Override
            public Predicate toPredicate(@NonNull Root<User> root, CriteriaQuery<?> query, @NonNull CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.notEqual(root.get("gender"), gender);
            }
        };
    }
}
