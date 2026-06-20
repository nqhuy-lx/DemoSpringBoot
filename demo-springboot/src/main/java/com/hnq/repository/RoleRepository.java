package com.hnq.repository;

import com.hnq.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    @Query(value = "select r from Role r inner join UserHasRole ur on r.id=ur.user.id where ur.user.id=: id")
    List<Role> getAllByUserId(Long id);
}
