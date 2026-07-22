package com.sparsh.freelancehub.tenant.dto;

import com.sparsh.freelancehub.common.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InviteMemberRequest {
    @Email(message = "Email should be valid")
    @NotNull
    private String email;

    @NotNull(message = "Role is required")
    private Role role;
}
