package com.example.demonats.repository;

import com.example.demonats.entity.Car;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarRepository extends JpaRepository<Car, Integer> {
    // 차량 번호 조회
    Optional<Car> findBySerialNumber(Integer serialNumber);

    // 차량 번호로 페이지 결과 조회
    Page<Car> findBySerialNumber(Integer serialNumber, Pageable pageable);
}
