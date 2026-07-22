package com.sparsh.freelancehub.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtpVerificationResponse {
    private Boolean verified;
    private String message;
}
