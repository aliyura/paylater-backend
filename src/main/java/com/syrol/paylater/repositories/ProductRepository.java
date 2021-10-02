package com.syrol.paylater.repositories;
import com.syrol.paylater.entities.Product;
import com.syrol.paylater.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByItemId(String id);
    Optional<Product> deleteByItemId(String itemId);
    Optional<List<Product>> findByStatus(Status status);
    @Query(value="SELECT * FROM products p WHERE (p.name like %:q% or description like %:q%  or product_type like %:q% or rate like %:q%) ",nativeQuery = true)
    Page<Product> findAllBySearch(Pageable pageable, String q);
    Optional<Page<Product>> findAllByProductType(String productType, Pageable pageable);
}