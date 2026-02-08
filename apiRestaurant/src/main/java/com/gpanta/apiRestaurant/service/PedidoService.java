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

    public Pedido crearPedido(CrearPedidoRequest request) {

        Mesa mesa = mesaRepository.findById(request.getMesaId())
                .orElseThrow(() -> new RuntimeException("Mesa no encontrada"));

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

    public Pedido agregarItem(Long pedidoId, ItemPedidoDTO dto) {

        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        MenuItem menu = menuItemRepository.findById(dto.getMenuItemId())
                .orElseThrow(() -> new RuntimeException("Plato no encontrado"));

        PedidoDetalle detalle = new PedidoDetalle();
        detalle.setPedido(pedido);
        detalle.setMenuItem(menu);
        detalle.setCantidad(dto.getCantidad());
        detalle.setPrecio(menu.getPrecio());

        detalleRepository.save(detalle);

        double nuevoTotal = pedido.getTotal() + (menu.getPrecio() * dto.getCantidad());
        pedido.setTotal(nuevoTotal);

        return pedidoRepository.save(pedido);
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

}

