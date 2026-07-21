package com.sparsh.freelancehub.tenant.repository;

import com.sparsh.freelancehub.tenant.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {
}
