package com.gpanta.apiRestaurant.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gpanta.apiRestaurant.service.EstadisticaService;

@RestController
@RequestMapping("/estadisticas")
@PreAuthorize("hasRole('ADMIN')")
public class EstadisticaController {

    @Autowired
    private EstadisticaService estadisticaService;

    @GetMapping("/hoy")
    public Map<String, Object> resumenHoy() {
        return estadisticaService.resumenHoy();
    }

    @GetMapping("/productos-mas-vendidos")
    public List<Map<String, Object>> productosMasVendidos() {
        return estadisticaService.productosMasVendidos();
    }
}

