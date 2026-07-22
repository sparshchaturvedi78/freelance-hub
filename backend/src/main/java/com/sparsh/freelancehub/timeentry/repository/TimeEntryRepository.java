package com.sparsh.freelancehub.timeentry.repository;

import com.sparsh.freelancehub.timeentry.entity.TimeEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TimeEntryRepository extends JpaRepository<TimeEntry, Long> {
    List<TimeEntry> findAllByOrganizationId(Long organizationId);
    List<TimeEntry> findAllByOrganizationIdAndProjectId(Long organizationId, Long projectId);
    List<TimeEntry> findAllByOrganizationIdAndUserId(Long organizationId, Long userId);
    Optional<TimeEntry> findByIdAndOrganizationId(Long id, Long organizationId);
}
