package com.sparsh.freelancehub.client.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientRequest {
    @NotBlank(message = "Client name is required")
    private String name;

    @Email(message = "Contact email should be valid")
    private String contactEmail;

    private String contactPhone;

    private String notes;
}
