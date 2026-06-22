package com.hnq.dto.request;

import jakarta.validation.constraints.Email;
import lombok.Getter;

@Getter
public class ForgotPasswordDTO {
    @Email(message = "email invalid format")
    private String email;
}
