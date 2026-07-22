package com.sparsh.freelancehub.auth.repository;

import com.sparsh.freelancehub.auth.entity.OtpRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface OtpRequestRepository extends JpaRepository<OtpRequest, Long> {

    Optional<OtpRequest> findByEmailAndPurposeAndIsActiveAndExpiresAtAfter(
            String email, OtpRequest.OtpPurpose purpose, Boolean isActive, Instant now);

    List<OtpRequest> findByEmailAndPurposeAndIsActive(
            String email, OtpRequest.OtpPurpose purpose, Boolean isActive);

    @Modifying
    @Query("UPDATE OtpRequest o SET o.isActive = false WHERE o.email = :email AND o.purpose = :purpose AND o.isActive = true")
    void deactivateAllForEmailAndPurpose(@Param("email") String email, @Param("purpose") OtpRequest.OtpPurpose purpose);

    @Modifying
    @Query("DELETE FROM OtpRequest o WHERE o.expiresAt < :now")
    void deleteExpiredOtps(@Param("now") Instant now);
}
