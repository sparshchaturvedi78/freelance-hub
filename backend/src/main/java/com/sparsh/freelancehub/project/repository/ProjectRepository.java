package com.sparsh.freelancehub.project.repository;

import com.sparsh.freelancehub.project.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findAllByOrganizationId(Long organizationId);
    List<Project> findAllByOrganizationIdAndClientId(Long organizationId, Long clientId);
    Optional<Project> findByIdAndOrganizationId(Long id, Long organizationId);
}
