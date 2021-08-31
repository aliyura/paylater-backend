package com.syrol.paylater.repositories;
import com.syrol.paylater.entities.User;
import com.syrol.paylater.entities.VerificationRequest;
import com.syrol.paylater.enums.AccountType;
import com.syrol.paylater.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationRepository extends JpaRepository<VerificationRequest, Long> {
    Optional<VerificationRequest> findByUsername(String username);
    void deleteById(Long id);
}
