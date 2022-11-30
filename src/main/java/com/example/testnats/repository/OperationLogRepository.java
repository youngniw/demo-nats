package com.example.testnats.repository;

import com.example.testnats.entity.OperationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OperationLogRepository extends JpaRepository<OperationLog, Long> {
    List<OperationLog> findAllByLogTimeBetweenOrderByLogTime(LocalDateTime startDate, LocalDateTime endDate);
}
