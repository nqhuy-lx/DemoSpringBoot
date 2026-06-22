package com.hnq.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class ResetPasswordDTO implements Serializable {
    @NotBlank(message = "key must be not blank")
    private String secretKey;

    @NotBlank(message = "password must be not blank")
    private String newPassword;
}
