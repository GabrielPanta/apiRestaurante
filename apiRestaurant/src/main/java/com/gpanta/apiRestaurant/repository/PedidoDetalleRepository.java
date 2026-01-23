package com.gpanta.apiRestaurant.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gpanta.apiRestaurant.model.PedidoDetalle;

public interface PedidoDetalleRepository extends JpaRepository<PedidoDetalle, Long> {
    List<PedidoDetalle> findByPedidoId(Long pedidoId);
}
