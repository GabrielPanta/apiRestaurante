package com.gpanta.apiRestaurant.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gpanta.apiRestaurant.model.Usuario;
import com.gpanta.apiRestaurant.repository.UsuarioRepository;

@Service
public class UsuarioService {
    

    @Autowired
    private  UsuarioRepository usuarioRepository;
    
    public List<Usuario>getAllUsuarios(){
        return usuarioRepository.findAll();
    }


}
