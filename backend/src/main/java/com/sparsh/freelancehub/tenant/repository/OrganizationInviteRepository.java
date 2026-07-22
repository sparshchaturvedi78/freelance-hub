package com.sparsh.freelancehub.tenant.repository;

import com.sparsh.freelancehub.tenant.entity.OrganizationInvite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrganizationInviteRepository extends JpaRepository<OrganizationInvite, Long> {
    List<OrganizationInvite> findAllByOrganizationId(Long organizationId);
    Optional<OrganizationInvite> findByToken(String token);
}
