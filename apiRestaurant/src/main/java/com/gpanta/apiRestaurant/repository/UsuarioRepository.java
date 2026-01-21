package com.gpanta.apiRestaurant.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gpanta.apiRestaurant.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario,Long> {
    Optional<Usuario> findByEmail(String email);
    List<Usuario> findByNombre(String nombre);
    List<Usuario> findByRol(String rol);
}

