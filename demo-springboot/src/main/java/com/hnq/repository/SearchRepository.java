package com.hnq.repository;

import com.hnq.dto.response.PageResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
}
