package com.hnq.repository;

import com.hnq.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
//    //distinct
//    // select distinct from User u where u.firstName=:firstName and u.lastName=:lastName
//    List<User> findDistinctByFirstNameAndLastName(String firstName, String lastName);
//    // select * from User u where u.email = ?1 (cach khac)
//    // singer field
//    List<User> findByEmail(String email);
//    // or
//    // select * from User u where u.firstName=:name or u.lastName=:name
//    List<User> findByFirstNameOrLastName(String name, String lastName);
//    // Is, Equals
//    // select * from User u where u.firstName=:name
//    List<User> findByFirstNameIs(String name);
//    List<User> findByFirstNameEquals(String name);
//    List<User> findByFirstName(String name);
//    // between
//    // select * from User u where u.createAt between ?1 and ?2
//    List<User> findByCreatedAtBetween(Date start, Date end);
//    // less than, greater than, equal
//    List<User> findByAgeLessThan(Integer age);
//    List<User> findByAgeLessThanEqual(Integer age);
//    List<User> findByAgeGreaterThan(Integer age);
//    List<User> findByAgeGreaterThanEqual(Integer age);
//    // Before, After
//    // select * from User u where u.createAt < :date
//    List<User> findByCreatedAtBefore(Date date);
//    // select * from User u where u.createAt > :date
//    List<User> findByCreatedAtAfter(Date date);
//    // Null, IsNull
//    // select * from User u where u.age is null
//    List<User> findByAgeIsNull();
//    // NotNull, IsNotNull
//    // select * from User u where u.age is not null
//    List<User> findByAgeNotNull();
//    // Like ko tu dong them % tuong tu equal
//    // not like
//    List<User> findByFirstNameLike(String name);
//    // StartingWith
//    // select * from User u where u.firstName like :name%
//    List<User> findByLastNameStartingWith(String name);
//    // EndingWith
//    // select * from User u where u.firstName like %:name
//    List<User> findByLastNameEndingWith(String name);
//    // Containing
//    // select * from User u where u.firstName like %:name%
//    List<User> findByLastNameContaining(String name);
//    // Not
//    // select * from User u where u.firstName <> %:name%
//    List<User> findByFirstNameNot(String name);
//    // In
//    // select * from User u where u.age in (18, 25, 30)
//    List<User> findByAgeIn(Collection<Integer> ages);
//    // NotIn
//    // select * from User u where u.age not in (18, 25, 30)
//    List<User> findByAgeNotIn(Collection<Integer> ages);
//    // True/False
//    // select * from User u where u.active = true
//    List<User> findByActiveTrue();
//    // IgnoreCase
//    // select * from User u where LOWER(u.firstName) = LOWER(:name)
//    List<User> findByFirstNameIgnoreCase(String name);
//    // order by
//    List<User> findByLastNameOrderByIdAsc(String name);
//
//    List<User> findByFirstNameAndLastNameAllIgnoreCase(String firstName, String lastName);

    Optional<User> findByUsername(String username);


}
