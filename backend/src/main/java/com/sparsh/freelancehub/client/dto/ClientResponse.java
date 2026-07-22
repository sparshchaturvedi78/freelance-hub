package com.sparsh.freelancehub.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientResponse {
    private Long id;
    private String name;
    private String contactEmail;
    private String contactPhone;
    private String notes;
    private Instant createdAt;
    private Instant updatedAt;
}
