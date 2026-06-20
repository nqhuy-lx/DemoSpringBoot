package com.hnq.service;

import com.hnq.dto.request.UserRequestDTO;
import com.hnq.dto.response.PageResponse;
import com.hnq.dto.response.UserDetailResponse;
import com.hnq.util.UserStatus;
import jakarta.mail.MessagingException;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.UnsupportedEncodingException;

public interface UserService {

    UserDetailsService userDetailsService();

    long saveUser(UserRequestDTO request) throws MessagingException, UnsupportedEncodingException;

    void updateUser(long userId, UserRequestDTO request);

    void changeStatus(long userId, UserStatus status);

    void deleteUser(long userId);

    UserDetailResponse getUser(long userId);

    PageResponse<?> getAllUsersWithSortBy(int page, int size, String sortBy);

    PageResponse<?> getAllUsersWithSortByAndSearch(int page, int size, String sortBy, String search);

    PageResponse<?> advanceSearchWithCriteria(int page, int size, String sortBy, String... search);

    PageResponse<?> advanceSearchWithSpecification(Pageable pageable, String[] user, String[] address);

    void confirmUser(int userId, String secretCode);
}
