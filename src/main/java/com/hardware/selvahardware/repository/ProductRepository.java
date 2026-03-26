package com.hardware.selvahardware.repository;

import com.hardware.selvahardware.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    Optional<Product> findByCode(String code);
    Optional<Product> findByBarcode(String barcode);
    boolean existsByCode(String code);

    @Query("SELECT p FROM Product p WHERE " +
           "(:category IS NULL OR p.category = :category) AND " +
           "(:search IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(p.code) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR p.barcode LIKE CONCAT('%', :search, '%'))")
    List<Product> searchProducts(@Param("category") String category, @Param("search") String search);

    @Query("SELECT p FROM Product p WHERE p.stock <= 5")
    List<Product> findLowStockProducts();

    @Query("SELECT p FROM Product p WHERE p.code = :q OR p.barcode = :q")
    Optional<Product> findByCodeOrBarcode(@Param("q") String q);
}
