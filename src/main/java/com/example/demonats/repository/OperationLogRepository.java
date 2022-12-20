package com.example.demonats.repository;

import com.example.demonats.entity.OperationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OperationLogRepository extends JpaRepository<OperationLog, Long> {
    // 모든 차량의 운행 정보 조회
    List<OperationLog> findAllByLogTimeBetweenOrderByLogTime(LocalDateTime startDate, LocalDateTime endDate);

    // 특정 차량의 운행 정보 조회
    @Query("select ol from OperationLog ol where ol.car.serialNumber = :serialNumber and ol.logTime between :startDate and :endDate order by ol.logTime")
    List<OperationLog> findLogBySerialNumberAndDate(LocalDateTime startDate, LocalDateTime endDate, Integer serialNumber);

    // 차량의 첫 운행 정보 조회
    Optional<OperationLog> findFirstByCar_SerialNumberOrderByLogTime(int serialNumber);

    // 차량 현재 최신 상태 조회
    Optional<OperationLog> findDistinctFirstByCar_SerialNumberOrderByLogTimeDesc(int serialNumber);

    // 차량의 대기중인 상태에 대한 최신 정보 조회
    Optional<OperationLog> findFirstByCar_SerialNumberAndGearIsOrderByLogTimeDesc(int serialNumber, int gear);

    // 차량의 운행중인 상태에 대한 최신 정보 조회
    Optional<OperationLog> findFirstByCar_SerialNumberAndGearIsNotOrderByLogTimeDesc(int serialNumber, int gear);
}
