package com.hnq.repository.criteria;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.function.Consumer;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserSearchCriteriaQueryConsumer implements Consumer<SearchCriteria> {

    private CriteriaBuilder criteriaBuilder;
    private Predicate predicate;
    private Root root;

    @Override
    public void accept(SearchCriteria searchCriteria) {
        if (searchCriteria.getOperation().equals(">")) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.greaterThanOrEqualTo(root.get(searchCriteria.getKey()), searchCriteria.getValue().toString()));
        } else if (searchCriteria.getOperation().equals("<")) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.lessThanOrEqualTo(root.get(searchCriteria.getKey()), searchCriteria.getValue().toString()));
        } else { // : => equal
            if (root.get(searchCriteria.getKey()).getJavaType().equals(String.class)) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(criteriaBuilder.lower(root.get(searchCriteria.getKey())), "%" + searchCriteria.getValue().toString().toLowerCase() + "%"));
            } else {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get(searchCriteria.getKey()), searchCriteria.getValue().toString()));
            }
        }
    }
}
