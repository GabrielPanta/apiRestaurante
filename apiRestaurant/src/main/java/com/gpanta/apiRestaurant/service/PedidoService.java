package com.gpanta.apiRestaurant.service;

import com.gpanta.apiRestaurant.dto.CrearPedidoRequest;
import com.gpanta.apiRestaurant.dto.ItemPedidoDTO;
import com.gpanta.apiRestaurant.model.EstadoMesa;
import com.gpanta.apiRestaurant.model.EstadoPedido;
import com.gpanta.apiRestaurant.model.MenuItem;
import com.gpanta.apiRestaurant.model.Mesa;
import com.gpanta.apiRestaurant.model.Pedido;
import com.gpanta.apiRestaurant.model.PedidoDetalle;
import com.gpanta.apiRestaurant.repository.MenuItemRepository;
import com.gpanta.apiRestaurant.repository.MesaRepository;
import com.gpanta.apiRestaurant.repository.PedidoDetalleRepository;
import com.gpanta.apiRestaurant.repository.PedidoRepository;

import com.gpanta.apiRestaurant.exception.BusinessException;
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

        if (mesa.getEstado() != EstadoMesa.LIBRE) {
            throw new BusinessException("La mesa no está disponible");
        }

        mesa.setEstado(EstadoMesa.OCUPADA);
        mesaRepository.save(mesa);

        Pedido pedido = new Pedido();
        pedido.setMesa(mesa);
        pedido.setFecha(LocalDateTime.now());
        pedido.setEstado(EstadoPedido.PENDIENTE);
        pedido.setTotal(0);

        pedido = pedidoRepository.save(pedido);

        double total = 0;

        for (ItemPedidoDTO item : request.getItems()) {

            MenuItem menu = menuItemRepository.findById(item.getMenuItemId())
                    .orElseThrow(() -> new RuntimeException("Plato no encontrado"));

            if (!menu.isDisponible()) {
                throw new BusinessException("El plato " + menu.getNombre() + " no está disponible");
            }

            if (item.getCantidad() <= 0) {
                throw new BusinessException("Cantidad inválida");
            }

            PedidoDetalle detalle = new PedidoDetalle();
            detalle.setPedido(pedido);
            detalle.setMenuItem(menu);
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecio(menu.getPrecio());
            detalle.setEstado(EstadoPedido.EN_PREPARACION);

            total += menu.getPrecio() * item.getCantidad();
            detalleRepository.save(detalle);
        }

        pedido.setTotal(total);
        pedido.setEstado(EstadoPedido.EN_PREPARACION);

        return pedidoRepository.save(pedido);
    }


    public List<Pedido> pedidosPorEstado(EstadoPedido estado) {
        return pedidoRepository.findByEstado(estado);
    }

    public Pedido cambiarEstado(Long id, EstadoPedido estado) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        pedido.setEstado(estado);

        // Si el pedido se marca como listo, marcamos todos sus items también
        if (estado == EstadoPedido.LISTO) {
            List<PedidoDetalle> items = detalleRepository.findByPedidoId(id);
            items.forEach(i -> {
                i.setEstado(EstadoPedido.LISTO);
                detalleRepository.save(i);
            });
        }
        return pedidoRepository.save(pedido);
    }



    public Pedido pedidoActivoPorMesa(Long mesaId) {

        List<EstadoPedido> estadosActivos = List.of(
                EstadoPedido.PENDIENTE,
                EstadoPedido.EN_PREPARACION,
                EstadoPedido.LISTO
        );

        return pedidoRepository
                .findByMesaIdAndEstadoIn(mesaId, estadosActivos)
                .stream()
                .findFirst()
                .orElse(null);
    }


    @Transactional
    public Pedido agregarItem(Long pedidoId, Long menuItemId, int cantidad) {

        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new BusinessException("Pedido no encontrado"));

        MenuItem menu = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new BusinessException("Plato no encontrado"));

        if (!menu.isDisponible()) {
            throw new BusinessException("El plato no está disponible");
        }

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
            // Asegura que el item sea visible para cocina
            detalle.setEstado(EstadoPedido.EN_PREPARACION); 
            detalleRepository.save(detalle);
        }

        // Si el pedido estaba LISTO, vuelve a EN_PREPARACION al añadir items
        if (pedido.getEstado() == EstadoPedido.LISTO) {
            pedido.setEstado(EstadoPedido.EN_PREPARACION);
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

        if (pedido.getEstado() == EstadoPedido.CERRADO) {
            throw new RuntimeException("El pedido ya está cerrado");
        }

        pedido.setEstado(EstadoPedido.CERRADO);

        Mesa mesa = pedido.getMesa();
        mesa.setEstado(EstadoMesa.LIBRE);
        mesaRepository.save(mesa);

        return pedidoRepository.save(pedido);
    }
}


