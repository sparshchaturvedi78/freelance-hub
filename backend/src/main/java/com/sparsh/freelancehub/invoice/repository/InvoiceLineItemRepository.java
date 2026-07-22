package com.sparsh.freelancehub.invoice.repository;

import com.sparsh.freelancehub.invoice.entity.InvoiceLineItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvoiceLineItemRepository extends JpaRepository<InvoiceLineItem, Long> {
    List<InvoiceLineItem> findAllByInvoiceId(Long invoiceId);
}
