package com.sparsh.freelancehub.client.repository;

import com.sparsh.freelancehub.client.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {
    List<Client> findAllByOrganizationId(Long organizationId);
    Optional<Client> findByIdAndOrganizationId(Long id, Long organizationId);
}
