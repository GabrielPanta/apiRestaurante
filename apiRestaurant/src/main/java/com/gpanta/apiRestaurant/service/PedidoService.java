package com.gpanta.apiRestaurant.service;

import com.gpanta.apiRestaurant.dto.CrearPedidoRequest;
import com.gpanta.apiRestaurant.dto.ItemPedidoDTO;
import com.gpanta.apiRestaurant.model.MenuItem;
import com.gpanta.apiRestaurant.model.Mesa;
import com.gpanta.apiRestaurant.model.Pedido;
import com.gpanta.apiRestaurant.model.PedidoDetalle;
import com.gpanta.apiRestaurant.repository.MenuItemRepository;
import com.gpanta.apiRestaurant.repository.MesaRepository;
import com.gpanta.apiRestaurant.repository.PedidoDetalleRepository;
import com.gpanta.apiRestaurant.repository.PedidoRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private PedidoDetalleRepository detalleRepository;

    @Autowired
    private MesaRepository mesaRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Transactional
    public Pedido crearPedido(CrearPedidoRequest request) {

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new RuntimeException("No se puede crear un pedido sin items");
        }

        Mesa mesa = mesaRepository.findById(request.getMesaId())
                .orElseThrow(() -> new RuntimeException("Mesa no encontrada"));

        if (!"LIBRE".equals(mesa.getEstado())) {
            throw new RuntimeException("La mesa no está disponible");
        }

        mesa.setEstado("OCUPADA");
        mesaRepository.save(mesa);

        Pedido pedido = new Pedido();
        pedido.setMesa(mesa);
        pedido.setFecha(LocalDateTime.now());
        pedido.setEstado("PENDIENTE");
        pedido.setTotal(0);

        pedido = pedidoRepository.save(pedido);

        double total = 0;

        for (ItemPedidoDTO item : request.getItems()) {

            MenuItem menu = menuItemRepository.findById(item.getMenuItemId())
                    .orElseThrow(() -> new RuntimeException("Plato no encontrado"));

            if (item.getCantidad() <= 0) {
                throw new RuntimeException("Cantidad inválida");
            }

            PedidoDetalle detalle = new PedidoDetalle();
            detalle.setPedido(pedido);
            detalle.setMenuItem(menu);
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecio(menu.getPrecio());

            total += menu.getPrecio() * item.getCantidad();
            detalleRepository.save(detalle);
        }

        pedido.setTotal(total);
        pedido.setEstado("EN_PREPARACION");

        return pedidoRepository.save(pedido);
    }

    public List<Pedido> pedidosPorEstado(String estado) {
        return pedidoRepository.findByEstado(estado);
    }

    public Pedido cambiarEstado(Long id, String estado) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        pedido.setEstado(estado);
        return pedidoRepository.save(pedido);
    }

    public Pedido pedidoActivoPorMesa(Long mesaId) {
        List<String> estadosActivos = List.of("PENDIENTE", "EN_PREPARACION", "LISTO");

        return pedidoRepository
                .findByMesaIdAndEstadoIn(mesaId, estadosActivos)
                .stream()
                .findFirst()
                .orElse(null);
    }

    @Transactional
    public Pedido agregarItem(Long pedidoId, Long menuItemId, int cantidad) {

        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        MenuItem menu = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new RuntimeException("Plato no encontrado"));

        PedidoDetalle detalle = detalleRepository
                .findByPedidoIdAndMenuItemId(pedidoId, menuItemId)
                .orElse(null);

        if (detalle == null) {
            detalle = new PedidoDetalle();
            detalle.setPedido(pedido);
            detalle.setMenuItem(menu);
            detalle.setCantidad(cantidad);
            detalle.setPrecio(menu.getPrecio());
        } else {
            detalle.setCantidad(detalle.getCantidad() + cantidad);
        }

        if (detalle.getCantidad() <= 0) {
            detalleRepository.delete(detalle);
        } else {
            detalleRepository.save(detalle);
        }

        recalcularTotal(pedido);

        return pedido;
    }

    private void recalcularTotal(Pedido pedido) {
        List<PedidoDetalle> items = detalleRepository.findByPedidoId(pedido.getId());

        double total = items.stream()
                .mapToDouble(i -> i.getPrecio() * i.getCantidad())
                .sum();

        pedido.setTotal(total);
        pedidoRepository.save(pedido);
    }

    public List<ItemPedidoDTO> listarPorPedido(Long pedidoId) {
        return detalleRepository.findByPedidoId(pedidoId)
                .stream()
                .map(detalle -> {
                    ItemPedidoDTO dto = new ItemPedidoDTO();
                    dto.setMenuItemId(detalle.getMenuItem().getId());
                    dto.setCantidad(detalle.getCantidad());
                    return dto;
                })
                .toList();
    }

    @Transactional
    public Pedido cerrarPedido(Long pedidoId) {

        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        if ("CERRADO".equals(pedido.getEstado())) {
            throw new RuntimeException("El pedido ya está cerrado");
        }

        pedido.setEstado("CERRADO");

        Mesa mesa = pedido.getMesa();
        mesa.setEstado("LIBRE");
        mesaRepository.save(mesa);

        return pedidoRepository.save(pedido);
    }

}

