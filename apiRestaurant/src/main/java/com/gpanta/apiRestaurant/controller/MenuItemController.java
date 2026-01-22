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
import org.springframework.web.bind.annotation.RestController;

import com.gpanta.apiRestaurant.model.MenuItem;
import com.gpanta.apiRestaurant.service.MenuItemService;

@RestController
@RequestMapping("/menu")
public class MenuItemController {
    @Autowired
    private MenuItemService menuItemService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public MenuItem crear(@RequestBody MenuItem item) {
        return menuItemService.crear(item);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MOZO','COCINA')")
    public List<MenuItem> listar() {
        return menuItemService.listarTodos();
    }

    @GetMapping("/disponibles")
    @PreAuthorize("hasAnyRole('ADMIN','MOZO','COCINA')")
    public List<MenuItem> disponibles() {
        return menuItemService.listarDisponibles();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public MenuItem actualizar(@PathVariable Long id,
                               @RequestBody MenuItem item) {
        return menuItemService.actualizar(id, item);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void eliminar(@PathVariable Long id) {
        menuItemService.eliminar(id);
    }
}
