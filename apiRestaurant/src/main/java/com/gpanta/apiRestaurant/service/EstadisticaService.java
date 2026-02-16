package com.gpanta.apiRestaurant.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gpanta.apiRestaurant.repository.PedidoDetalleRepository;
import com.gpanta.apiRestaurant.repository.PedidoRepository;

@Service
public class EstadisticaService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private PedidoDetalleRepository pedidoDetalleRepository;

    public Map<String, Object> resumenHoy() {

        Double total = pedidoRepository.totalVentasHoy();
        Long pedidos = pedidoRepository.cantidadPedidosHoy();

        double ticket = 0;
        if (pedidos != null && pedidos > 0) {
            ticket = total / pedidos;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("totalVentas", total);
        data.put("cantidadPedidos", pedidos);
        data.put("ticketPromedio", ticket);

        return data;
    }

    public List<Map<String, Object>> productosMasVendidos() {

        List<Object[]> rows = pedidoDetalleRepository.productosMasVendidos();

        List<Map<String, Object>> lista = new ArrayList<>();

        for (Object[] r : rows) {
            Map<String, Object> item = new HashMap<>();
            item.put("nombre", r[0]);
            item.put("cantidad", r[1]);
            lista.add(item);
        }

        return lista;
    }
}

