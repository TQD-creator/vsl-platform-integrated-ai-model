package com.capstone.vsl.repository;

import com.capstone.vsl.entity.Report;
import com.capstone.vsl.entity.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    
    List<Report> findByStatus(ReportStatus status);
    
    List<Report> findByUserOrderByCreatedAtDesc(com.capstone.vsl.entity.User user);
}

