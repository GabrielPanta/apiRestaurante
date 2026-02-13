package com.gpanta.apiRestaurant.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gpanta.apiRestaurant.model.EstadoMesa;
import com.gpanta.apiRestaurant.model.Mesa;
import com.gpanta.apiRestaurant.service.MesaService;

@RestController
@RequestMapping("/mesas")
public class MesaController {
     @Autowired
    private MesaService mesaService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Mesa crear(@RequestBody Mesa mesa) {
        return mesaService.crear(mesa);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MOZO')")
    public List<Mesa> listar() {
        return mesaService.listar();
    }

    @PutMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('ADMIN','MOZO')")
    public Mesa cambiarEstado(@PathVariable Long id,
                              @RequestParam EstadoMesa estado) {
        return mesaService.cambiarEstado(id, estado);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void eliminar(@PathVariable Long id) {
        mesaService.eliminar(id);
    }

}
