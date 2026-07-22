package com.sparsh.freelancehub.client.service;

import com.sparsh.freelancehub.client.dto.ClientRequest;
import com.sparsh.freelancehub.client.dto.ClientResponse;
import com.sparsh.freelancehub.client.entity.Client;
import com.sparsh.freelancehub.client.repository.ClientRepository;
import com.sparsh.freelancehub.common.exception.ApiException;
import com.sparsh.freelancehub.security.UserPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClientService {
    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public Page<ClientResponse> getClients(UserPrincipal principal, Pageable pageable) {
        return clientRepository.findAllByOrganizationId(principal.getOrganizationId(), pageable)
                .map(this::mapToResponse);
    }

    public ClientResponse getClient(UserPrincipal principal, Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ApiException("Client not found", HttpStatus.NOT_FOUND));

        if (!client.getOrganizationId().equals(principal.getOrganizationId())) {
            throw new ApiException("Unauthorized access to client", HttpStatus.FORBIDDEN);
        }

        return mapToResponse(client);
    }

    @Transactional
    public ClientResponse createClient(UserPrincipal principal, ClientRequest request) {
        Client client = Client.builder()
                .organizationId(principal.getOrganizationId())
                .name(request.getName())
                .contactEmail(request.getContactEmail())
                .notes(request.getNotes())
                .build();

        Client saved = clientRepository.save(client);
        return mapToResponse(saved);
    }

    @Transactional
    public ClientResponse updateClient(UserPrincipal principal, Long clientId, ClientRequest request) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ApiException("Client not found", HttpStatus.NOT_FOUND));

        if (!client.getOrganizationId().equals(principal.getOrganizationId())) {
            throw new ApiException("Unauthorized access to client", HttpStatus.FORBIDDEN);
        }

        client.setName(request.getName());
        client.setContactEmail(request.getContactEmail());
        client.setNotes(request.getNotes());

        Client saved = clientRepository.save(client);
        return mapToResponse(saved);
    }

    @Transactional
    public void deleteClient(UserPrincipal principal, Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ApiException("Client not found", HttpStatus.NOT_FOUND));

        if (!client.getOrganizationId().equals(principal.getOrganizationId())) {
            throw new ApiException("Unauthorized access to client", HttpStatus.FORBIDDEN);
        }

        clientRepository.delete(client);
    }

    private ClientResponse mapToResponse(Client client) {
        return ClientResponse.builder()
                .id(client.getId())
                .name(client.getName())
                .contactEmail(client.getContactEmail())
                .notes(client.getNotes())
                .createdAt(client.getCreatedAt())
                .updatedAt(client.getUpdatedAt())
                .build();
    }
}
