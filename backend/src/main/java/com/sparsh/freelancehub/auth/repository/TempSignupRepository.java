package com.sparsh.freelancehub.auth.repository;

import com.sparsh.freelancehub.auth.entity.TempSignup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface TempSignupRepository extends JpaRepository<TempSignup, Long> {

    Optional<TempSignup> findByEmailAndExpiresAtAfter(String email, Instant now);

    Optional<TempSignup> findByEmail(String email);

    @Modifying
    void deleteByEmail(String email);

    @Modifying
    @Query("DELETE FROM TempSignup t WHERE t.expiresAt < :now")
    void deleteExpired(@Param("now") Instant now);
}
