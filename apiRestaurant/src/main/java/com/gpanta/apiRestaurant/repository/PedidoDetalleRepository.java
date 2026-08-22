package com.gpanta.apiRestaurant.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.gpanta.apiRestaurant.model.EstadoPedido;
import com.gpanta.apiRestaurant.model.PedidoDetalle;

public interface PedidoDetalleRepository extends JpaRepository<PedidoDetalle, Long> {
    List<PedidoDetalle> findByPedidoId(Long pedidoId);
    Optional<PedidoDetalle> findByPedidoIdAndMenuItemId(Long pedidoId, Long menuItemId);
    
    @Query("""
            SELECT pd.menuItem.nombre, SUM(pd.cantidad)
            FROM PedidoDetalle pd
            WHERE pd.pedido.estado = :estado
            GROUP BY pd.menuItem.nombre
            ORDER BY SUM(pd.cantidad) DESC
            """)
    List<Object[]> productosMasVendidos(@Param("estado") EstadoPedido estado);

    @Query("""
            SELECT pd.menuItem.nombre, SUM(pd.cantidad)
            FROM PedidoDetalle pd
            WHERE pd.pedido.fecha >= :inicio AND pd.pedido.fecha <= :fin AND pd.pedido.estado = :estado
            GROUP BY pd.menuItem.nombre
            ORDER BY SUM(pd.cantidad) DESC
            """)
    List<Object[]> productosMasVendidosEnRango(@Param("inicio") java.time.LocalDateTime inicio, @Param("fin") java.time.LocalDateTime fin, @Param("estado") EstadoPedido estado);
}
