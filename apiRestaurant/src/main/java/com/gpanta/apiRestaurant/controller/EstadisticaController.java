package com.gpanta.apiRestaurant.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping("/rango")
    public Map<String, Object> resumenRango(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        if (inicio == null) inicio = LocalDate.now();
        if (fin == null) fin = LocalDate.now();
        return estadisticaService.resumenRango(inicio, fin);
    }

    @GetMapping("/productos-mas-vendidos")
    public List<Map<String, Object>> productosMasVendidos() {
        return estadisticaService.productosMasVendidos();
    }

    @GetMapping("/productos-mas-vendidos/rango")
    public List<Map<String, Object>> productosMasVendidosRango(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        return estadisticaService.productosMasVendidosEnRango(inicio, fin);
    }
}

