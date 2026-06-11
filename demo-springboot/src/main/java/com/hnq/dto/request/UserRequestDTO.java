package com.hnq.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hnq.util.EnumPattern;
import com.hnq.util.PhoneNumber;
import com.hnq.util.UserStatus;
import jakarta.validation.constraints.*;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
public class UserRequestDTO implements Serializable {
    @NotBlank(message = "first name must be not blank")
    private String firstName;

    @NotNull(message = "last name must be not null")
    private String lastName;

    @Email(message = "email invalid format")
    private String email;

    //@Pattern(regexp = "^\\d{10}$", message = "phone invalid format")
    @PhoneNumber
    private String phone;

    @NotNull(message = "dateOfBirth must be not null")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date dateOfBirth;

    @NotEmpty(message = "addresses can not empty")
    private List<String> addresses;

    @EnumPattern(name = "status", regexp = "ACTIVE|INACTIVE|NONE")
    private UserStatus status;

    public UserRequestDTO(String phone, String email, String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
    }

}
