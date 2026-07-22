package com.sparsh.freelancehub.client.controller;

import com.sparsh.freelancehub.client.dto.ClientRequest;
import com.sparsh.freelancehub.client.dto.ClientResponse;
import com.sparsh.freelancehub.client.service.ClientService;
import com.sparsh.freelancehub.common.dto.ApiResponse;
import com.sparsh.freelancehub.security.UserPrincipal;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clients")
public class ClientController {
    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ClientResponse>>> getClients(
            @AuthenticationPrincipal UserPrincipal principal,
            Pageable pageable) {
        Page<ClientResponse> clients = clientService.getClients(principal, pageable);
        return ResponseEntity.ok(ApiResponse.ok(clients));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ClientResponse>> getClient(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id) {
        ClientResponse client = clientService.getClient(principal, id);
        return ResponseEntity.ok(ApiResponse.ok(client));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ClientResponse>> createClient(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody ClientRequest request) {
        ClientResponse client = clientService.createClient(principal, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(client));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ClientResponse>> updateClient(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id,
            @Valid @RequestBody ClientRequest request) {
        ClientResponse client = clientService.updateClient(principal, id, request);
        return ResponseEntity.ok(ApiResponse.ok(client));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteClient(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id) {
        clientService.deleteClient(principal, id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
