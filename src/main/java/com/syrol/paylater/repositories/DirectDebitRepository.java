package com.syrol.paylater.repositories;
import com.syrol.paylater.entities.DirectDebit;
import com.syrol.paylater.entities.PLService;
import com.syrol.paylater.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DirectDebitRepository extends JpaRepository<DirectDebit, Long> {

}
