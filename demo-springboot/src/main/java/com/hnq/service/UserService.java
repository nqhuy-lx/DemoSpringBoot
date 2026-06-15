package com.hnq.service;

import com.hnq.dto.request.UserRequestDTO;
import com.hnq.dto.response.PageResponse;
import com.hnq.dto.response.UserDetailResponse;
import com.hnq.util.UserStatus;

import java.util.List;

public interface UserService {
    long saveUser(UserRequestDTO request);

    void updateUser(long userId, UserRequestDTO request);

    void changeStatus(long userId, UserStatus status);

    void deleteUser(long userId);

    UserDetailResponse getUser(long userId);

    PageResponse<?> getAllUsers(int page, int size, String sortBy);
}
