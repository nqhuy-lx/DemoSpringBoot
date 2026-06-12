package com.hnq.controller;

import com.hnq.component.Translator;
import com.hnq.dto.request.UserRequestDTO;
import com.hnq.dto.respone.ResponeData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j// log
@RestController
@RequestMapping("/user")
@Tag(name = "User controller")
public class UserController {
    @Operation(method = "POST", summary = "Add new user", description = "Send a request via this API to create new user")
    @PostMapping(value = "/", headers = "apiKey=v1.0")
    public ResponeData<Integer> addUser(@Valid @RequestBody UserRequestDTO request){
//        log.info("Adding user {} {}", request.getFirstName(), request.getLastName());
        return new ResponeData<>(HttpStatus.CREATED.value(), Translator.toLocale("user.add.success"), 1);
    }

    @PutMapping("{userId}")
    public String updateUser(@Min(1) @PathVariable int userId, @Valid @RequestBody UserRequestDTO requets){
        System.out.println("update user success = " + userId);
        return "update user success";
    }

    @PatchMapping("{userId}")
    public String changeStatus(@PathVariable int userId, @RequestParam(required = false) Boolean status){
        System.out.println("change user status = " + userId);
        return "patch user success";
    }

    @DeleteMapping("{userId}")
    public String deleteUser(@PathVariable int userId){
        System.out.println("delete user have id = " + userId);
        return "delete user success";
    }

    @GetMapping("{userId}")
    public ResponeData<UserRequestDTO> getUser(@PathVariable int userId){
        System.out.println("get user have id = " + userId);
        UserRequestDTO u = new UserRequestDTO("phone", "email", "huy", "nguyen");
        return new ResponeData<>(HttpStatus.CREATED.value(), "User detail", null);
    }

    @GetMapping("/")
    public ResponeData<List<UserRequestDTO>> getUsers(){
        List<UserRequestDTO> users = new ArrayList<>();
        users.add(new UserRequestDTO("phone", "email", "huy", "nguyen"));
        users.add(new UserRequestDTO("phone", "email", "huy", "nguyen"));
        users.add(new UserRequestDTO("phone", "email", "huy", "nguyen"));
        return new  ResponeData<>(HttpStatus.CREATED.value(), "User list", users);
    }
}
