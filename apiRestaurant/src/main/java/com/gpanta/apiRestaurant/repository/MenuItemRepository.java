package com.gpanta.apiRestaurant.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gpanta.apiRestaurant.model.MenuItem;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    List<MenuItem> findByDisponibleTrue();
    
}
