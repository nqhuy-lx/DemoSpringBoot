package com.hnq.repository;

import com.hnq.dto.response.PageResponse;
import com.hnq.model.User;
import com.hnq.repository.criteria.SearchCriteria;
import com.hnq.repository.criteria.UserSearchCriteriaQueryConsumer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Repository
public class SearchRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public PageResponse<?> getAllUsersWithSortByAndSearch(int page, int size, String sortBy, String search) {
        StringBuilder sqlQuery = new StringBuilder("select new com.hnq.dto.response.UserDetailResponse(u.id, u.firstName, u.lastName, u.email, u.phone) from User u where 1=1 ");
        if (StringUtils.hasText(search)) {
            sqlQuery.append(" and lower(u.firstName) like lower(:firstName)");
            sqlQuery.append(" or lower(u.lastName) like lower(:lastName)");
            sqlQuery.append(" or lower(u.email) like lower(:email)");
        }
        if(StringUtils.hasText(sortBy)) {
            Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
            Matcher matcher = pattern.matcher(sortBy);
            if (matcher.find()) {
                sqlQuery.append(String.format(" order by u.%s %s", matcher.group(1), matcher.group(3)));
            }
        }

        Query query = entityManager.createQuery(sqlQuery.toString());
        if (page < 0)
            page = 0;
        query.setFirstResult(page * size);
        query.setMaxResults(size);
        if (StringUtils.hasText(search)) {
            query.setParameter("firstName", String.format("%%%s%%", search));
            query.setParameter("lastName", String.format("%%%s%%", search));
            query.setParameter("email", String.format("%%%s%%", search));
        }
        List users = query.getResultList();

        System.out.println("users: " + users);

        StringBuilder sqlCountQuery = new StringBuilder("select count(u) from User u where 1=1 ");
        if (StringUtils.hasText(search)) {
            sqlCountQuery.append(" and lower(u.firstName) like lower(?1)");
            sqlCountQuery.append(" or lower(u.lastName) like lower(?2)");
            sqlCountQuery.append(" or lower(u.email) like lower(?3)");
        }

        Query queryCount = entityManager.createQuery(sqlCountQuery.toString());
        if (StringUtils.hasText(search)) {
            queryCount.setParameter(1, String.format("%%%s%%", search));
            queryCount.setParameter(2, String.format("%%%s%%", search));
            queryCount.setParameter(3, String.format("%%%s%%", search));
        }
        Long total = (Long) queryCount.getSingleResult();
        System.out.println("total: " + total);
        Page<?> p  = new PageImpl<Object>(users, PageRequest.of(page, size), total);
        return PageResponse.builder()
                .pageNo(page)
                .pageSize(size)
                .totalPage(p.getTotalPages())
                .items(p.stream().toList())
                .build();
    }

    public PageResponse<?> advanceSearchUser(int page, int size, String sortBy, String... search){
        // firstName:huy, lastName:Nguyen (param search)
        List<SearchCriteria> criteriaList = new ArrayList<>();
        // 1. list user
        if (search != null) {
            log.info("search user by criteria");
            for(String s: search) {
                Pattern pattern = Pattern.compile("(\\w+?)(:|<|>)(.*)");
                Matcher matcher = pattern.matcher(s);
                if (matcher.find()) {
                    criteriaList.add(new SearchCriteria(matcher.group(1), matcher.group(2), matcher.group(3)));
                }
            }
        }
        // 2. total record
        List<User> users = getUsers(page, size, criteriaList, sortBy);

        return PageResponse.builder()
                .pageNo(page)
                .pageSize(size)
                .totalPage(0)
                .items(users)
                .build();
    }

    private List<User> getUsers(int page, int size, List<SearchCriteria> criteriaList, String sortBy) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
        Root<User> userRoot = criteriaQuery.from(User.class);

        // search
        Predicate predicate = criteriaBuilder.conjunction();
        UserSearchCriteriaQueryConsumer consumer = new UserSearchCriteriaQueryConsumer(criteriaBuilder, predicate, userRoot);

        criteriaList.forEach(consumer);
        predicate = consumer.getPredicate();

        criteriaQuery.where(predicate);
        //sort
        if (StringUtils.hasText(sortBy)) {
            Pattern pattern = Pattern.compile("(\\w+?)(:)(asc|desc)");
            Matcher matcher = pattern.matcher(sortBy);
            if (matcher.find()) {
                String columnName = matcher.group(1);
                if(matcher.group(3).equalsIgnoreCase("desc"))
                    criteriaQuery.orderBy(criteriaBuilder.desc(userRoot.get(columnName)));
                else
                    criteriaQuery.orderBy(criteriaBuilder.asc(userRoot.get(columnName)));
            }
        }
        return entityManager.createQuery(criteriaQuery).setFirstResult(page  * size).setMaxResults(size).getResultList();
    }
}
