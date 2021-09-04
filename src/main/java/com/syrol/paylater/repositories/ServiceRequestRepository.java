package com.syrol.paylater.repositories;
import com.syrol.paylater.entities.ServiceRequest;
import com.syrol.paylater.enums.ServiceTenureType;
import com.syrol.paylater.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {
    Optional<List<ServiceRequest>> findBySuid(String id);
    Optional<List<ServiceRequest>> findByUuid(String id);
    Optional<ServiceRequest> findBySruid(String id);
    Optional<ServiceRequest> findByOrderReference(String reference);
    Optional<Page<ServiceRequest>> findByStatus(Status status, Pageable Pageable);
    Optional<Page<ServiceRequest>> findByTenure(ServiceTenureType tenure, Pageable Pageable);
    @Query(value="SELECT * FROM service_request s WHERE  s.suid=:suid and s.uuid=:uuid",nativeQuery = true)
    Optional<List<ServiceRequest>> findBySuidAndUser(String suid, String uuid);
    void deleteById(Long id);

}
