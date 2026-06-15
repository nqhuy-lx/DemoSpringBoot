package com.hnq.controller;

import com.hnq.component.Translator;
import com.hnq.dto.request.UserRequestDTO;
import com.hnq.dto.response.ResponseData;
import com.hnq.dto.response.ResponseError;
import com.hnq.dto.response.UserDetailResponse;
import com.hnq.exception.ResourceNotFoundException;
import com.hnq.service.UserService;
import com.hnq.util.UserStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j // log
@RestController
@RequestMapping("/users")
@Tag(name = "User controller")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(method = "POST", summary = "Add new user", description = "Send a request via this API to create new user")
    @PostMapping(value = "/")
    public ResponseData<Long> addUser(@Valid @RequestBody UserRequestDTO request) {
        log.info("Request add user, {} {}", request.getFirstName(), request.getLastName());

        try {
            long userId = userService.saveUser(request);
            return new ResponseData<>(HttpStatus.CREATED.value(), Translator.toLocale("user.add.success"), userId);
        } catch (Exception e) {
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Add user fail");
        }
    }

    @PutMapping("/{userId}")
    public ResponseData<?> updateUser(@Min(1) @PathVariable long userId, @Valid @RequestBody UserRequestDTO request) {
        log.info("Request update userId={}", userId);

        try {
            userService.updateUser(userId, request);
            return new ResponseData<>(HttpStatus.ACCEPTED.value(), Translator.toLocale("user.upd.success"));
        } catch (Exception e) {
            log.error("failed to update user errorMessage={}", e.getMessage());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Update user fail");
        }
    }

    @PatchMapping("/{userId}/change-status")
    public ResponseData<Void> changeStatus(@PathVariable int userId, @RequestParam(required = false) UserStatus status) {
        log.info("Request change status, userId={}", userId);

        try {
            userService.changeStatus(userId, status);
            return new ResponseData<>(HttpStatus.ACCEPTED.value(), Translator.toLocale("user.change.success"));
        } catch (Exception e) {
            log.error("failed to change status user, errorMessage={}", e.getMessage());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Change status fail");
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseData<Void> deleteUser(@PathVariable @Min(value = 1, message = "userId must be greater than 0") long userId) {
        log.info("Request delete userId={}", userId);

        try {
            userService.deleteUser(userId);
            return new ResponseData<>(HttpStatus.NO_CONTENT.value(), Translator.toLocale("user.del.success"));
        } catch (Exception e) {
            log.error("failed to delete user, errorMessage={}", e.getMessage());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Delete user fail");
        }
    }

    @GetMapping("/{userId}")
    public ResponseData<UserDetailResponse> getUser(@PathVariable @Min(1) long userId) {
        log.info("Request get user detail, userId={}", userId);

        try {
            UserDetailResponse user = userService.getUser(userId);
            return new ResponseData<>(HttpStatus.OK.value(), "get user detail", user);
        } catch (ResourceNotFoundException e) {
            log.error("failed to get user detail, userId={}, errorMessage={}", userId, e.getMessage());
            return new ResponseError(HttpStatus.NOT_FOUND.value(), e.getMessage());
        }
//        UserDetailResponse user = userService.getUser(userId);
//        return new ResponseData<>(HttpStatus.OK.value(), "get user detail", user);
    }

    @GetMapping("/")
    public ResponseData<?> getAllUsers(@RequestParam(defaultValue = "0", required = false) int pageNo,
                                                              @Min(10) @RequestParam(defaultValue = "20", required = false) int pageSize,
                                                              @RequestParam(required = false) String sortBy) {
        log.info("Request get user list, pageNo={}, pageSize={}", pageNo, pageSize);
        return new ResponseData<>(HttpStatus.CREATED.value(), "User list", userService.getAllUsers(pageNo, pageSize,  sortBy));
    }
}
