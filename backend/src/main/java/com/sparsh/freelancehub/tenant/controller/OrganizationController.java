package com.sparsh.freelancehub.tenant.controller;

import com.sparsh.freelancehub.common.dto.ApiResponse;
import com.sparsh.freelancehub.security.UserPrincipal;
import com.sparsh.freelancehub.tenant.dto.InviteMemberRequest;
import com.sparsh.freelancehub.tenant.dto.OrganizationResponse;
import com.sparsh.freelancehub.tenant.dto.UpdateOrganizationRequest;
import com.sparsh.freelancehub.tenant.service.OrganizationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/organizations")
public class OrganizationController {
    private final OrganizationService organizationService;

    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @GetMapping("/current")
    public ResponseEntity<ApiResponse<OrganizationResponse>> getCurrentOrganization(
            @AuthenticationPrincipal UserPrincipal principal) {
        OrganizationResponse response = organizationService.getOrganization(principal);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/current")
    public ResponseEntity<ApiResponse<OrganizationResponse>> updateOrganization(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody UpdateOrganizationRequest request) {
        OrganizationResponse response = organizationService.updateOrganization(principal, request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/invite")
    public ResponseEntity<ApiResponse<Void>> inviteMember(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody InviteMemberRequest request) {
        organizationService.inviteMember(principal, request);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @PostMapping("/invites/{token}/accept")
    public ResponseEntity<ApiResponse<Void>> acceptInvite(@PathVariable String token) {
        organizationService.acceptInvite(token);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
