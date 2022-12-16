package com.example.demonats.repository;

import com.example.demonats.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarRepository extends JpaRepository<Car, Integer> {
    Optional<Car> findBySerialNumber(Integer serialNumber);
}
