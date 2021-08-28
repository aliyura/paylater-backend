package com.syrol.paylater.repositories;
import com.syrol.paylater.entities.PLService;
import com.syrol.paylater.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceRepository extends JpaRepository<PLService, Long> {
    Optional<PLService> findBySuid(String suid);
    Optional<List<PLService>> findByStatus(Status status);
    Optional<PLService> findByTitle(String title);
    void deleteById(Long id);
}
