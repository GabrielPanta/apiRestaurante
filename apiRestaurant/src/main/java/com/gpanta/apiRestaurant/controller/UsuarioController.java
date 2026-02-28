package com.gpanta.apiRestaurant.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gpanta.apiRestaurant.model.Usuario;
import com.gpanta.apiRestaurant.service.UsuarioService;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {


    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Usuario> getAllUsuarios() {
        return usuarioService.getAllUsuarios();
    }
}
