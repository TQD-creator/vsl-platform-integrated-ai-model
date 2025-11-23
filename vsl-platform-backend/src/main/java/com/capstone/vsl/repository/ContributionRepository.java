package com.capstone.vsl.repository;

import com.capstone.vsl.entity.Contribution;
import com.capstone.vsl.entity.ContributionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContributionRepository extends JpaRepository<Contribution, Long> {
    
    List<Contribution> findByStatus(ContributionStatus status);
    
    long countByStatus(ContributionStatus status);
    
    Optional<Contribution> findByIdAndStatus(Long id, ContributionStatus status);
}

