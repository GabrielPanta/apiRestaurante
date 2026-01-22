package com.gpanta.apiRestaurant.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gpanta.apiRestaurant.model.Mesa;

public interface MesaRepository extends JpaRepository<Mesa, Long> {
    
}
