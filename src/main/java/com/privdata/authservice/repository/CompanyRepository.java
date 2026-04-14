package com.privdata.authservice.repository;

import com.privdata.authservice.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CompanyRepository extends JpaRepository<Company, UUID> {
    Optional<Company> findByTaxId(String taxId);
    boolean existsByTaxId(String taxId);
}