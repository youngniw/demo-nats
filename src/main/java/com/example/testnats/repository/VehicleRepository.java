package com.example.testnats.repository;

import com.example.testnats.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {
    Optional<Vehicle> findBySerialNumber(Integer serialNumber);
}
