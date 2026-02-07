package com.gpanta.apiRestaurant.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gpanta.apiRestaurant.model.Mesa;
import com.gpanta.apiRestaurant.repository.MesaRepository;

@Service
public class MesaService  {
    
    @Autowired
    private MesaRepository mesaRepository;

    public Mesa crear(Mesa mesa) {

        if (mesaRepository.existsByNumero(mesa.getNumero())) {

            throw new RuntimeException("Número de mesa ya existe");
        }
        mesa.setEstado("LIBRE");
        return mesaRepository.save(mesa);
    }

    public List<Mesa> listar() {
        return mesaRepository.findAll();
    }

    public Mesa cambiarEstado(Long id, String estado) {
        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mesa no encontrada"));
        mesa.setEstado(estado);
        return mesaRepository.save(mesa);
    }

    public void eliminar(Long id) {
        if (!mesaRepository.existsById(id)) {
            throw new RuntimeException("La mesa no existe");
        }
        mesaRepository.deleteById(id);
    }



}
