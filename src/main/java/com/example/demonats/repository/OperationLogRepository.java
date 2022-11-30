package com.example.demonats.repository;

import com.example.demonats.entity.OperationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OperationLogRepository extends JpaRepository<OperationLog, Long> {
    List<OperationLog> findAllByLogTimeBetweenOrderByLogTime(LocalDateTime startDate, LocalDateTime endDate);
}
