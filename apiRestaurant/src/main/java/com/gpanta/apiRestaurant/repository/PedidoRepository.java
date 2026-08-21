package com.gpanta.apiRestaurant.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.gpanta.apiRestaurant.model.EstadoPedido;
import com.gpanta.apiRestaurant.model.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByEstado(EstadoPedido estado);

    List<Pedido> findByMesaIdAndEstadoIn(Long mesaId, List<EstadoPedido> estados);

    @Query("SELECT COALESCE(SUM(p.total), 0.0) FROM Pedido p WHERE p.fecha >= :inicio AND p.fecha <= :fin AND p.estado = :estado")
    Double totalVentasEnRango(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin, @Param("estado") EstadoPedido estado);

    @Query("SELECT COUNT(p) FROM Pedido p WHERE p.fecha >= :inicio AND p.fecha <= :fin AND p.estado = :estado")
    Long cantidadPedidosEnRango(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin, @Param("estado") EstadoPedido estado);
}
