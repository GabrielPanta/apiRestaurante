package com.gpanta.apiRestaurant.dto;

import java.util.List;

import lombok.Data;

@Data
public class CrearPedidoRequest {
    private Long mesaId;
    private List<ItemPedidoDTO> items;
}
