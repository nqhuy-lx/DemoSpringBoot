package com.hnq.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ResetDTO {
    @NotBlank(message = "key must be not blank")
    private String secretKey;
}
