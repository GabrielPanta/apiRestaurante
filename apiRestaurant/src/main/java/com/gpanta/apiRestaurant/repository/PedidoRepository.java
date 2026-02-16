package com.gpanta.apiRestaurant.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.gpanta.apiRestaurant.model.EstadoPedido;
import com.gpanta.apiRestaurant.model.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByEstado(EstadoPedido estado);

    List<Pedido> findByMesaIdAndEstadoIn(Long mesaId, List<EstadoPedido> estados);

    @Query("SELECT COALESCE(SUM(p.total),0) FROM Pedido p WHERE DATE(p.fecha) = CURRENT_DATE AND p.estado = 'CERRADO'")
    Double totalVentasHoy();

    @Query("SELECT COUNT(p) FROM Pedido p WHERE DATE(p.fecha) = CURRENT_DATE AND p.estado = 'CERRADO'")
    Long cantidadPedidosHoy();
}
