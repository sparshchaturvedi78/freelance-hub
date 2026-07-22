package com.sparsh.freelancehub.project.controller;

import com.sparsh.freelancehub.common.dto.ApiResponse;
import com.sparsh.freelancehub.project.dto.ProjectRequest;
import com.sparsh.freelancehub.project.dto.ProjectResponse;
import com.sparsh.freelancehub.project.service.ProjectService;
import com.sparsh.freelancehub.security.UserPrincipal;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {
    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProjectResponse>>> getProjects(
            @AuthenticationPrincipal UserPrincipal principal,
            Pageable pageable) {
        Page<ProjectResponse> projects = projectService.getProjects(principal, pageable);
        return ResponseEntity.ok(ApiResponse.ok(projects));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjectResponse>> getProject(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id) {
        ProjectResponse project = projectService.getProject(principal, id);
        return ResponseEntity.ok(ApiResponse.ok(project));
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<ApiResponse<Page<ProjectResponse>>> getClientProjects(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long clientId,
            Pageable pageable) {
        Page<ProjectResponse> projects = projectService.getClientProjects(principal, clientId, pageable);
        return ResponseEntity.ok(ApiResponse.ok(projects));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProjectResponse>> createProject(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody ProjectRequest request) {
        ProjectResponse project = projectService.createProject(principal, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(project));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjectResponse>> updateProject(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id,
            @Valid @RequestBody ProjectRequest request) {
        ProjectResponse project = projectService.updateProject(principal, id, request);
        return ResponseEntity.ok(ApiResponse.ok(project));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProject(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id) {
        projectService.deleteProject(principal, id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
