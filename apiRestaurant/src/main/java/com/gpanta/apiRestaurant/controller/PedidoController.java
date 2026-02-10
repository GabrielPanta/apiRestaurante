package com.gpanta.apiRestaurant.controller;

import com.gpanta.apiRestaurant.dto.CrearPedidoRequest;
import com.gpanta.apiRestaurant.model.Pedido;
import com.gpanta.apiRestaurant.model.PedidoDetalle;
import com.gpanta.apiRestaurant.repository.PedidoDetalleRepository;
import com.gpanta.apiRestaurant.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoDetalleRepository pedidoDetalleRepository;

    @Autowired
    private PedidoService pedidoService;

    PedidoController(PedidoDetalleRepository pedidoDetalleRepository) {
        this.pedidoDetalleRepository = pedidoDetalleRepository;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('MOZO','ADMIN')")
    public Pedido crear(@RequestBody CrearPedidoRequest request) {
        return pedidoService.crearPedido(request);
    }

    @GetMapping("/cocina")
    @PreAuthorize("hasAnyRole('MOZO','ADMIN','COCINA')")
    public List<Pedido> pedidosCocina() {
        return pedidoService.pedidosPorEstado("EN_PREPARACION");
    }

    @PutMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('COCINA','CAJERO')")
    public Pedido cambiarEstado(@PathVariable Long id,
                                @RequestParam String estado) {
        return pedidoService.cambiarEstado(id, estado);
    }

    @GetMapping("/mesa/{mesaId}/activo")
    @PreAuthorize("hasAnyRole('MOZO','ADMIN')")
    public Pedido pedidoActivo(@PathVariable Long mesaId) {
        return pedidoService.pedidoActivoPorMesa(mesaId);
    }

    @PostMapping("/{pedidoId}/items")
    @PreAuthorize("hasAnyRole('MOZO','ADMIN')")
    public Pedido agregarItem(
            @PathVariable Long pedidoId,
            @RequestParam Long menuItemId,
            @RequestParam int cantidad) {

        return pedidoService.agregarItem(pedidoId, menuItemId, cantidad);
    }

    @GetMapping("/{pedidoId}/items")
    public List<PedidoDetalle> listarItems(@PathVariable Long pedidoId) {
        return pedidoDetalleRepository.findByPedidoId(pedidoId);
    }

    @GetMapping("/{pedidoId}/detalles")
    @PreAuthorize("hasAnyRole('COCINA','ADMIN')")
    public List<PedidoDetalle> detalles(@PathVariable Long pedidoId) {
        return pedidoDetalleRepository.findByPedidoId(pedidoId);
    }

    @PutMapping("/{id}/cerrar")
    @PreAuthorize("hasAnyRole('MOZO','ADMIN','COCINA')")
    public Pedido cerrarPedido(@PathVariable Long id) {
        return pedidoService.cerrarPedido(id);
    }


}

