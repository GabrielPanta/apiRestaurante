package com.gpanta.apiRestaurant.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gpanta.apiRestaurant.model.MenuItem;
import com.gpanta.apiRestaurant.repository.MenuItemRepository;

@Service
public class MenuItemService {
    @Autowired
    private MenuItemRepository menuItemRepository;

    public MenuItem crear(MenuItem item) {
        item.setDisponible(true);
        return menuItemRepository.save(item);
    }

    public List<MenuItem> listarTodos() {
        return menuItemRepository.findAll();
    }

    public List<MenuItem> listarDisponibles() {
        return menuItemRepository.findByDisponibleTrue();
    }

    public MenuItem actualizar(Long id, MenuItem data) {
        MenuItem item = menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plato no encontrado"));

        item.setNombre(data.getNombre());
        item.setPrecio(data.getPrecio());
        item.setDisponible(data.isDisponible());

        return menuItemRepository.save(item);
    }

    public void eliminar(Long id) {
        menuItemRepository.deleteById(id);
    }
}
