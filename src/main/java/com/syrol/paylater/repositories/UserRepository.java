package com.syrol.paylater.repositories;
import com.syrol.paylater.entities.User;
import com.syrol.paylater.enums.AccountType;
import com.syrol.paylater.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByMobile(String mobile);
    Optional<User> findByUuid(String uuid);
    Optional<User> findByBvn(String bvn);
    Page<User> findByStatus(Pageable pageable, Status status);
    Page<User> findByReferralCode(Pageable pageable, String code);
    Page<User> findAllByAccountType(Pageable pageable, AccountType type);
}
