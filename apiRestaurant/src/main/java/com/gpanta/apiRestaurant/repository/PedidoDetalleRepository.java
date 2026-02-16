package com.gpanta.apiRestaurant.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.gpanta.apiRestaurant.model.PedidoDetalle;

public interface PedidoDetalleRepository extends JpaRepository<PedidoDetalle, Long> {
    List<PedidoDetalle> findByPedidoId(Long pedidoId);
    Optional<PedidoDetalle> findByPedidoIdAndMenuItemId(Long pedidoId, Long menuItemId);
    
    @Query("""
            SELECT pd.menuItem.nombre, SUM(pd.cantidad)
            FROM PedidoDetalle pd
            WHERE pd.pedido.estado = 'CERRADO'
            GROUP BY pd.menuItem.nombre
            ORDER BY SUM(pd.cantidad) DESC
            """)
    List<Object[]> productosMasVendidos();
}
