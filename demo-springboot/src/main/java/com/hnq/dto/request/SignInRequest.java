package com.hnq.dto.request;

import com.hnq.util.Platform;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class SignInRequest implements Serializable {
    @NotBlank(message = "username must be not blank")
    private String username;

    @NotBlank(message = "password must be not blank")
    private String password;

    @NotNull(message = "platform must be not null")
    private Platform platform; // gui tu thiet bi nao

    private String deviceToken;  // put notify

    private String version; // app o version nao
}
