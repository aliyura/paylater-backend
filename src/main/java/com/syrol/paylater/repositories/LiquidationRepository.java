package com.syrol.paylater.repositories;
import com.syrol.paylater.entities.Liquidation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LiquidationRepository extends JpaRepository<Liquidation, Long> {
    Optional<Liquidation> findByRequestId(String requestId);
    Optional<List<Liquidation>> findByOrderReference(String orderReference);
    Optional<List<Liquidation>> findByUuid(String uuid);
}
