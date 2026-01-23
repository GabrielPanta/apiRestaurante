package com.gpanta.apiRestaurant.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gpanta.apiRestaurant.model.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByEstado(String estado);
}
