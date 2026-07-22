package com.sparsh.freelancehub.project.dto;

import com.sparsh.freelancehub.project.entity.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectResponse {
    private Long id;
    private Long clientId;
    private String name;
    private ProjectStatus status;
    private BigDecimal hourlyRate;
    private String description;
    private Instant createdAt;
    private Instant updatedAt;
}
