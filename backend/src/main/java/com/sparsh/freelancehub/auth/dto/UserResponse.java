package com.sparsh.freelancehub.auth.dto;

import com.sparsh.freelancehub.common.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private Long id;
    private String email;
    private String fullName;
    private Role role;
    private Long organizationId;
}
