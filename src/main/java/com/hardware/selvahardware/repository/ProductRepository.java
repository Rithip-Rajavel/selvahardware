package com.hardware.selvahardware.repository;

import com.hardware.selvahardware.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    Optional<Product> findByCode(String code);
    Optional<Product> findByBarcode(String barcode);
    boolean existsByCode(String code);

    @Query(value = "SELECT * FROM products p WHERE " +
       "(:category IS NULL OR p.category = :category) AND " +
       "(:search IS NULL OR p.name ILIKE CONCAT('%', cast(:search as text), '%') " +
       "OR p.code ILIKE CONCAT('%', cast(:search as text), '%') " +
       "OR p.barcode ILIKE CONCAT('%', cast(:search as text), '%'))",
       nativeQuery = true)
    List<Product> searchProducts(String category, String search);

    @Query("SELECT p FROM Product p WHERE p.stock <= 5")
    List<Product> findLowStockProducts();

    @Query("SELECT p FROM Product p WHERE p.code = :q OR p.barcode = :q")
    Optional<Product> findByCodeOrBarcode(@Param("q") String q);
}
