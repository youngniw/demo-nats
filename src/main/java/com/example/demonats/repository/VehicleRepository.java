package com.example.demonats.repository;

import com.example.demonats.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {
    Optional<Vehicle> findBySerialNumber(Integer serialNumber);
}
