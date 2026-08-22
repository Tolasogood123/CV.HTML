package com.farmers.general.marketplace.repository;

import com.farmers.general.marketplace.entity.Product;
import com.farmers.general.marketplace.enums.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByDeletedFalseAndStatus(ProductStatus status);

    List<Product> findBySellerIdAndDeletedFalse(Long sellerId);

    List<Product> findBySellerIdAndStatusAndDeletedFalse(Long sellerId, ProductStatus status);

    List<Product> findByDeletedFalse();
}
