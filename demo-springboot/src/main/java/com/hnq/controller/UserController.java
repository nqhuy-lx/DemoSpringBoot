package com.hnq.controller;

import com.hnq.dto.request.UserRequestDTO;
import com.hnq.dto.respone.ResponeData;
import com.hnq.dto.respone.ResponeSuccess;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    @Operation(method = "POST", summary = "Add new user", description = "Send a request via this API to create new user")
    @PostMapping(value = "/", headers = "apikey=v1.0")
    public ResponeData<Integer> addUser(@Valid @RequestBody UserRequestDTO requets){
        return new ResponeData<>(HttpStatus.CREATED.value(), "User Added", 1);
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
