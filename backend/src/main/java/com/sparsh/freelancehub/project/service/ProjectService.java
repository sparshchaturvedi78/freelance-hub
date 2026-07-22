package com.sparsh.freelancehub.project.service;

import com.sparsh.freelancehub.client.repository.ClientRepository;
import com.sparsh.freelancehub.common.exception.ApiException;
import com.sparsh.freelancehub.project.dto.ProjectRequest;
import com.sparsh.freelancehub.project.dto.ProjectResponse;
import com.sparsh.freelancehub.project.entity.Project;
import com.sparsh.freelancehub.project.entity.ProjectStatus;
import com.sparsh.freelancehub.project.repository.ProjectRepository;
import com.sparsh.freelancehub.security.UserPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ClientRepository clientRepository;

    public ProjectService(ProjectRepository projectRepository, ClientRepository clientRepository) {
        this.projectRepository = projectRepository;
        this.clientRepository = clientRepository;
    }

    public Page<ProjectResponse> getProjects(UserPrincipal principal, Pageable pageable) {
        return projectRepository.findAllByOrganizationId(principal.getOrganizationId(), pageable)
                .map(this::mapToResponse);
    }

    public Page<ProjectResponse> getClientProjects(UserPrincipal principal, Long clientId, Pageable pageable) {
        clientRepository.findByIdAndOrganizationId(clientId, principal.getOrganizationId())
                .orElseThrow(() -> new ApiException("Client not found", HttpStatus.NOT_FOUND));

        return projectRepository.findAllByClientIdAndOrganizationId(clientId, principal.getOrganizationId(), pageable)
                .map(this::mapToResponse);
    }

    public ProjectResponse getProject(UserPrincipal principal, Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ApiException("Project not found", HttpStatus.NOT_FOUND));

        if (!project.getOrganizationId().equals(principal.getOrganizationId())) {
            throw new ApiException("Unauthorized access to project", HttpStatus.FORBIDDEN);
        }

        return mapToResponse(project);
    }

    @Transactional
    public ProjectResponse createProject(UserPrincipal principal, ProjectRequest request) {
        clientRepository.findByIdAndOrganizationId(request.getClientId(), principal.getOrganizationId())
                .orElseThrow(() -> new ApiException("Client not found", HttpStatus.NOT_FOUND));

        Project project = Project.builder()
                .organizationId(principal.getOrganizationId())
                .clientId(request.getClientId())
                .name(request.getName())
                .status(request.getStatus() != null ? request.getStatus() : ProjectStatus.ACTIVE)
                .hourlyRate(request.getHourlyRate())
                .description(request.getDescription())
                .build();

        Project saved = projectRepository.save(project);
        return mapToResponse(saved);
    }

    @Transactional
    public ProjectResponse updateProject(UserPrincipal principal, Long projectId, ProjectRequest request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ApiException("Project not found", HttpStatus.NOT_FOUND));

        if (!project.getOrganizationId().equals(principal.getOrganizationId())) {
            throw new ApiException("Unauthorized access to project", HttpStatus.FORBIDDEN);
        }

        if (!project.getClientId().equals(request.getClientId())) {
            clientRepository.findByIdAndOrganizationId(request.getClientId(), principal.getOrganizationId())
                    .orElseThrow(() -> new ApiException("Client not found", HttpStatus.NOT_FOUND));
        }

        project.setName(request.getName());
        project.setClientId(request.getClientId());
        project.setStatus(request.getStatus() != null ? request.getStatus() : project.getStatus());
        project.setHourlyRate(request.getHourlyRate());
        project.setDescription(request.getDescription());

        Project saved = projectRepository.save(project);
        return mapToResponse(saved);
    }

    @Transactional
    public void deleteProject(UserPrincipal principal, Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ApiException("Project not found", HttpStatus.NOT_FOUND));

        if (!project.getOrganizationId().equals(principal.getOrganizationId())) {
            throw new ApiException("Unauthorized access to project", HttpStatus.FORBIDDEN);
        }

        projectRepository.delete(project);
    }

    private ProjectResponse mapToResponse(Project project) {
        return ProjectResponse.builder()
                .id(project.getId())
                .clientId(project.getClientId())
                .name(project.getName())
                .status(project.getStatus())
                .hourlyRate(project.getHourlyRate())
                .description(project.getDescription())
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .build();
    }
}
