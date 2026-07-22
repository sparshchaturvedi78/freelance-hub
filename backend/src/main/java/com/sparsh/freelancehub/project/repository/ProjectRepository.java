package com.sparsh.freelancehub.project.repository;

import com.sparsh.freelancehub.project.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findAllByOrganizationId(Long organizationId);
    Page<Project> findAllByOrganizationId(Long organizationId, Pageable pageable);
    List<Project> findAllByOrganizationIdAndClientId(Long organizationId, Long clientId);
    Page<Project> findAllByClientIdAndOrganizationId(Long clientId, Long organizationId, Pageable pageable);
    Optional<Project> findByIdAndOrganizationId(Long id, Long organizationId);
}
