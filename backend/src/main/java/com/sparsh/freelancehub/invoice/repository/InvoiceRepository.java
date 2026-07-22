package com.sparsh.freelancehub.invoice.repository;

import com.sparsh.freelancehub.invoice.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findAllByOrganizationId(Long organizationId);
    Optional<Invoice> findByOrganizationIdAndInvoiceNumber(Long organizationId, String invoiceNumber);
    Optional<Invoice> findByIdAndOrganizationId(Long id, Long organizationId);
}
