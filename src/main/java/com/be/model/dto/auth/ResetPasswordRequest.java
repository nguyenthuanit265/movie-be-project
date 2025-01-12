package com.be.model.dto.auth;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResetPasswordRequest {
    private String token;

    @NotEmpty
    @Size(min = 6, max = 32)
    private String newPassword;

    @NotEmpty
    private String confirmPassword;
}
