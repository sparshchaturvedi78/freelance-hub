package com.sparsh.freelancehub.tenant.service;

import com.sparsh.freelancehub.auth.entity.User;
import com.sparsh.freelancehub.auth.repository.UserRepository;
import com.sparsh.freelancehub.common.enums.Role;
import com.sparsh.freelancehub.common.exception.ApiException;
import com.sparsh.freelancehub.security.UserPrincipal;
import com.sparsh.freelancehub.tenant.dto.InviteMemberRequest;
import com.sparsh.freelancehub.tenant.dto.OrganizationResponse;
import com.sparsh.freelancehub.tenant.dto.UpdateOrganizationRequest;
import com.sparsh.freelancehub.tenant.entity.InviteStatus;
import com.sparsh.freelancehub.tenant.entity.Organization;
import com.sparsh.freelancehub.tenant.entity.OrganizationInvite;
import com.sparsh.freelancehub.tenant.repository.OrganizationInviteRepository;
import com.sparsh.freelancehub.tenant.repository.OrganizationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class OrganizationService {
    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final OrganizationInviteRepository inviteRepository;

    public OrganizationService(OrganizationRepository organizationRepository,
                              UserRepository userRepository,
                              OrganizationInviteRepository inviteRepository) {
        this.organizationRepository = organizationRepository;
        this.userRepository = userRepository;
        this.inviteRepository = inviteRepository;
    }

    public OrganizationResponse getOrganization(UserPrincipal principal) {
        Organization org = organizationRepository.findById(principal.getOrganizationId())
                .orElseThrow(() -> new ApiException("Organization not found", HttpStatus.NOT_FOUND));
        return mapToResponse(org);
    }

    @Transactional
    public OrganizationResponse updateOrganization(UserPrincipal principal, UpdateOrganizationRequest request) {
        Organization org = organizationRepository.findById(principal.getOrganizationId())
                .orElseThrow(() -> new ApiException("Organization not found", HttpStatus.NOT_FOUND));

        if (!principal.getRole().equals(Role.OWNER.name())) {
            throw new ApiException("Only organization owner can update organization", HttpStatus.FORBIDDEN);
        }

        org.setName(request.getName());
        organizationRepository.save(org);
        return mapToResponse(org);
    }

    @Transactional
    public void inviteMember(UserPrincipal principal, InviteMemberRequest request) {
        if (!principal.getRole().equals(Role.OWNER.name()) && !principal.getRole().equals(Role.ADMIN.name())) {
            throw new ApiException("Only owner or admin can invite members", HttpStatus.FORBIDDEN);
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ApiException("User with this email already exists", HttpStatus.BAD_REQUEST);
        }

        String inviteToken = UUID.randomUUID().toString();
        OrganizationInvite invite = OrganizationInvite.builder()
                .organizationId(principal.getOrganizationId())
                .email(request.getEmail())
                .role(request.getRole())
                .token(inviteToken)
                .invitedBy(principal.getId())
                .status(InviteStatus.PENDING)
                .expiresAt(Instant.now().plusSeconds(604800))
                .build();
        inviteRepository.save(invite);
    }

    @Transactional
    public void acceptInvite(String token) {
        OrganizationInvite invite = inviteRepository.findByToken(token)
                .orElseThrow(() -> new ApiException("Invalid or expired invite", HttpStatus.BAD_REQUEST));

        if (invite.getStatus() != InviteStatus.PENDING) {
            throw new ApiException("Invite has already been used", HttpStatus.BAD_REQUEST);
        }

        if (invite.getExpiresAt().isBefore(Instant.now())) {
            throw new ApiException("Invite has expired", HttpStatus.BAD_REQUEST);
        }

        if (userRepository.findByEmail(invite.getEmail()).isPresent()) {
            throw new ApiException("User with this email already exists", HttpStatus.BAD_REQUEST);
        }

        invite.setStatus(InviteStatus.ACCEPTED);
        inviteRepository.save(invite);
    }

    private OrganizationResponse mapToResponse(Organization org) {
        return OrganizationResponse.builder()
                .id(org.getId())
                .name(org.getName())
                .createdAt(org.getCreatedAt())
                .updatedAt(org.getUpdatedAt())
                .build();
    }
}