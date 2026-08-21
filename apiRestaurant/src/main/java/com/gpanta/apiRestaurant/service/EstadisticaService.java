package com.gpanta.apiRestaurant.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gpanta.apiRestaurant.model.EstadoPedido;
import com.gpanta.apiRestaurant.repository.PedidoDetalleRepository;
import com.gpanta.apiRestaurant.repository.PedidoRepository;

@Service
public class EstadisticaService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private PedidoDetalleRepository pedidoDetalleRepository;

    public Map<String, Object> resumenHoy() {
        LocalDateTime inicio = LocalDate.now().atStartOfDay();
        LocalDateTime fin = LocalDate.now().atTime(LocalTime.MAX);

        Double total = pedidoRepository.totalVentasEnRango(inicio, fin, EstadoPedido.CERRADO);
        Long pedidos = pedidoRepository.cantidadPedidosEnRango(inicio, fin, EstadoPedido.CERRADO);

        double totalVal = (total != null) ? total : 0.0;
        long pedidosVal = (pedidos != null) ? pedidos : 0L;

        double ticket = (pedidosVal > 0) ? (totalVal / pedidosVal) : 0.0;

        Map<String, Object> data = new HashMap<>();
        data.put("totalVentas", totalVal);
        data.put("cantidadPedidos", pedidosVal);
        data.put("ticketPromedio", ticket);
        data.put("fecha", LocalDate.now().toString());

        return data;
    }

    public List<Map<String, Object>> productosMasVendidos() {

        List<Object[]> rows = pedidoDetalleRepository.productosMasVendidos(EstadoPedido.CERRADO);

        List<Map<String, Object>> lista = new ArrayList<>();

        if (rows != null) {
            for (Object[] r : rows) {
                Map<String, Object> item = new HashMap<>();
                item.put("nombre", r[0]);
                item.put("cantidad", r[1]);
                lista.add(item);
            }
        }

        return lista;
    }
}

