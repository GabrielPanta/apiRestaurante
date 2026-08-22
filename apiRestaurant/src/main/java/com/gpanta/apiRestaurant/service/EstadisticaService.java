package com.gpanta.apiRestaurant.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gpanta.apiRestaurant.model.EstadoPedido;
import com.gpanta.apiRestaurant.model.Pedido;
import com.gpanta.apiRestaurant.repository.PedidoDetalleRepository;
import com.gpanta.apiRestaurant.repository.PedidoRepository;

@Service
public class EstadisticaService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private PedidoDetalleRepository pedidoDetalleRepository;

    public Map<String, Object> resumenHoy() {
        return resumenRango(LocalDate.now(), LocalDate.now());
    }

    public Map<String, Object> resumenRango(LocalDate fechaInicio, LocalDate fechaFin) {
        if (fechaInicio == null) fechaInicio = LocalDate.now();
        if (fechaFin == null) fechaFin = LocalDate.now();

        LocalDateTime inicio = fechaInicio.atStartOfDay();
        LocalDateTime fin = fechaFin.atTime(LocalTime.MAX);

        Double total = pedidoRepository.totalVentasEnRango(inicio, fin, EstadoPedido.CERRADO);
        Long pedidos = pedidoRepository.cantidadPedidosEnRango(inicio, fin, EstadoPedido.CERRADO);

        double totalVal = (total != null) ? total : 0.0;
        long pedidosVal = (pedidos != null) ? pedidos : 0L;
        double ticket = (pedidosVal > 0) ? (totalVal / pedidosVal) : 0.0;

        List<Pedido> pedidosList = pedidoRepository.pedidosCerradosEnRango(inicio, fin, EstadoPedido.CERRADO);
        Map<String, Double> ventasPorDia = new LinkedHashMap<>();
        Map<String, Long> pedidosPorDia = new LinkedHashMap<>();

        if (pedidosList != null) {
            for (Pedido p : pedidosList) {
                if (p.getFecha() != null) {
                    String dia = p.getFecha().toLocalDate().toString();
                    ventasPorDia.put(dia, ventasPorDia.getOrDefault(dia, 0.0) + p.getTotal());
                    pedidosPorDia.put(dia, pedidosPorDia.getOrDefault(dia, 0L) + 1);
                }
            }
        }

        List<Map<String, Object>> serieTemporal = new ArrayList<>();
        ventasPorDia.forEach((dia, totalDia) -> {
            Map<String, Object> punto = new HashMap<>();
            punto.put("fecha", dia);
            punto.put("total", totalDia);
            punto.put("pedidos", pedidosPorDia.getOrDefault(dia, 0L));
            serieTemporal.add(punto);
        });

        Map<String, Object> data = new HashMap<>();
        data.put("totalVentas", totalVal);
        data.put("cantidadPedidos", pedidosVal);
        data.put("ticketPromedio", ticket);
        data.put("fechaInicio", fechaInicio.toString());
        data.put("fechaFin", fechaFin.toString());
        data.put("serieTemporal", serieTemporal);

        return data;
    }

    public List<Map<String, Object>> productosMasVendidos() {
        return productosMasVendidosEnRango(null, null);
    }

    public List<Map<String, Object>> productosMasVendidosEnRango(LocalDate fechaInicio, LocalDate fechaFin) {
        List<Object[]> rows;
        if (fechaInicio != null && fechaFin != null) {
            LocalDateTime inicio = fechaInicio.atStartOfDay();
            LocalDateTime fin = fechaFin.atTime(LocalTime.MAX);
            rows = pedidoDetalleRepository.productosMasVendidosEnRango(inicio, fin, EstadoPedido.CERRADO);
        } else {
            rows = pedidoDetalleRepository.productosMasVendidos(EstadoPedido.CERRADO);
        }

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

