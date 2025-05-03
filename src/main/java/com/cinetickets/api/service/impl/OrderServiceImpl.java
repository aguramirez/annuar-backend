package com.cinetickets.api.service.impl;

import com.cinetickets.api.dto.request.OrderRequest;
import com.cinetickets.api.dto.response.OrderDetailsResponse;
import com.cinetickets.api.dto.response.OrderResponse;
import com.cinetickets.api.dto.response.ShowResponse;
import com.cinetickets.api.entity.*;
import com.cinetickets.api.exception.ResourceNotFoundException;
import com.cinetickets.api.repository.*;
import com.cinetickets.api.service.OrderService;
import com.cinetickets.api.service.PromotionService;
import com.cinetickets.api.service.QrCodeService;
import com.cinetickets.api.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationService reservationService;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ComboRepository comboRepository;
    private final PromotionService promotionService;
    private final QrCodeService qrCodeService;
    private final ShowRepository showRepository;

    @Override
    @Transactional
    public OrderResponse createOrder(OrderRequest orderRequest, UUID userId) {
        // Validar que exista la reserva
        if (orderRequest.getReservationId() == null) {
            throw new IllegalArgumentException("Reservation ID is required");
        }
        
        Reservation reservation = reservationRepository.findById(orderRequest.getReservationId())
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", "id", orderRequest.getReservationId()));
        
        // Validar que la reserva sea del usuario (si es usuario autenticado)
        if (userId != null && reservation.getUser() != null && !reservation.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Reservation does not belong to the user");
        }
        
        // Validar que la reserva esté pendiente
        if (reservation.getStatus() != Reservation.ReservationStatus.PENDING) {
            throw new IllegalArgumentException("Reservation is not in pending status");
        }
        
        // Obtener el usuario (si existe)
        User user = userId != null ? userRepository.findById(userId).orElse(null) : null;
        
        // Procesar los items adicionales (productos, combos)
        List<OrderItem> orderItems = processOrderItems(orderRequest, null);
        
        // Calcular subtotal de la reserva (entradas)
        BigDecimal subtotalTickets = calculateTicketsSubtotal(reservation);
        
        // Calcular subtotal de productos y combos
        BigDecimal subtotalItems = calculateItemsSubtotal(orderItems);
        
        // Calcular subtotal total
        BigDecimal subtotal = subtotalTickets.add(subtotalItems);
        
        // Aplicar promoción si existe
        BigDecimal discount = BigDecimal.ZERO;
        if (orderRequest.getPromotionCode() != null && !orderRequest.getPromotionCode().isEmpty()) {
            discount = promotionService.applyPromotion(orderRequest.getPromotionCode(), subtotal, userId);
        }
        
        // Calcular impuestos (en este ejemplo no se aplican)
        BigDecimal tax = BigDecimal.ZERO;
        
        // Calcular total
        BigDecimal total = subtotal.subtract(discount).add(tax);
        
        // Crear la orden
        Order order = Order.builder()
                .id(UUID.randomUUID())
                .user(user)
                .reservation(reservation)
                .subtotal(subtotal)
                .discount(discount)
                .tax(tax)
                .total(total)
                .paymentMethod(orderRequest.getPaymentMethod())
                .paymentStatus(Order.PaymentStatus.PENDING)
                .orderType(Order.OrderType.ONLINE)
                .notes(orderRequest.getNotes())
                .status(Order.OrderStatus.PENDING)
                .createdAt(ZonedDateTime.now())
                .updatedAt(ZonedDateTime.now())
                .build();
        
        // Añadir los items a la orden
        orderItems.forEach(item -> item.setOrder(order));
        order.setItems(orderItems);
        
        // Guardar la orden
        Order savedOrder = orderRepository.save(order);
        
        // Confirmar la reserva
        reservationService.confirmReservation(reservation.getId());
        
        return mapToOrderResponse(savedOrder);
    }

    @Override
    @Transactional
    public OrderResponse createPosOrder(OrderRequest orderRequest, UUID operatorId) {
        // Para órdenes de taquilla, puede haber o no reserva previa
        Reservation reservation = null;
        User operator = userRepository.findById(operatorId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", operatorId));
        
        // Si hay reserva previa, obtenerla
        if (orderRequest.getReservationId() != null) {
            reservation = reservationRepository.findById(orderRequest.getReservationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Reservation", "id", orderRequest.getReservationId()));
        } 
        // Si no hay reserva, crear una nueva reserva directa
        else if (orderRequest.getShowId() != null && orderRequest.getSeats() != null && !orderRequest.getSeats().isEmpty()) {
            // En una implementación real, aquí crearías una reserva directa
            // Para simplificar, asumimos que ya existe la reserva
            throw new IllegalArgumentException("Direct reservation creation not implemented in this version");
        } else {
            throw new IllegalArgumentException("Either reservationId or showId with seats must be provided");
        }
        
        // Procesar los items de la orden
        List<OrderItem> orderItems = processOrderItems(orderRequest, null);
        
        // Calcular subtotal de la reserva (entradas)
        BigDecimal subtotalTickets = calculateTicketsSubtotal(reservation);
        
        // Calcular subtotal de productos y combos
        BigDecimal subtotalItems = calculateItemsSubtotal(orderItems);
        
        // Calcular subtotal total
        BigDecimal subtotal = subtotalTickets.add(subtotalItems);
        
        // Aplicar promoción si existe
        BigDecimal discount = BigDecimal.ZERO;
        if (orderRequest.getPromotionCode() != null && !orderRequest.getPromotionCode().isEmpty()) {
            discount = promotionService.applyPromotion(orderRequest.getPromotionCode(), subtotal, null);
        }
        
        // Calcular impuestos
        BigDecimal tax = BigDecimal.ZERO;
        
        // Calcular total
        BigDecimal total = subtotal.subtract(discount).add(tax);
        
        // Crear la orden
        Order order = Order.builder()
                .id(UUID.randomUUID())
                .user(reservation.getUser()) // Usuario que reservó o null
                .reservation(reservation)
                .operator(operator)
                .subtotal(subtotal)
                .discount(discount)
                .tax(tax)
                .total(total)
                .paymentMethod(orderRequest.getPaymentMethod())
                .paymentStatus(Order.PaymentStatus.PAID) // En taquilla, el pago es inmediato
                .orderType(Order.OrderType.IN_PERSON)
                .notes(orderRequest.getNotes())
                .status(Order.OrderStatus.COMPLETED)
                .createdAt(ZonedDateTime.now())
                .updatedAt(ZonedDateTime.now())
                .build();
        
        // Generar código QR para la entrada
        String qrCode = qrCodeService.generateTicketQrCode(
                order.getId(), 
                reservation.getShow().getId(),
                System.currentTimeMillis()
        );
        order.setQrCode(qrCode);
        
        // Añadir los items a la orden
        orderItems.forEach(item -> item.setOrder(order));
        order.setItems(orderItems);
        
        // Guardar la orden
        Order savedOrder = orderRepository.save(order);
        
        return mapToOrderResponse(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDetailsResponse getOrderDetailsById(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        
        return mapToOrderDetailsResponse(order, false);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDetailsResponse getOrderDetailsById(UUID orderId, UUID userId) {
        Order order;
        
        if (userId != null) {
            // Usuario normal solo puede ver sus propias órdenes
            order = orderRepository.findByIdAndUserId(orderId, userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        } else {
            // Staff puede ver cualquier orden
            order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        }
        
        return mapToOrderDetailsResponse(order, false);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDetailsResponse getOrderDetailsWithQrById(UUID orderId, UUID userId) {
        Order order;
        
        if (userId != null) {
            // Usuario normal solo puede ver sus propias órdenes
            order = orderRepository.findByIdAndUserId(orderId, userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        } else {
            // Staff puede ver cualquier orden
            order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        }
        
        // Solo se puede ver el QR de órdenes pagadas
        if (order.getPaymentStatus() != Order.PaymentStatus.PAID) {
            throw new IllegalStateException("Cannot get tickets for unpaid order");
        }
        
        return mapToOrderDetailsResponse(order, true);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getUserOrders(UUID userId, Pageable pageable) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(this::mapToOrderResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getAllOrders(UUID cinemaId, String status, Pageable pageable) {
        // Aquí se implementaría filtrado basado en cinemaId y status
        // Para simplificar, retornamos todas las órdenes
        return orderRepository.findAll(pageable)
                .map(this::mapToOrderResponse);
    }

    @Override
    @Transactional
    public void cancelOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        
        // Solo se pueden cancelar órdenes pendientes o completadas pero no usadas
        if (order.getStatus() == Order.OrderStatus.PENDING || order.getStatus() == Order.OrderStatus.COMPLETED) {
            order.setStatus(Order.OrderStatus.CANCELED);
            order.setUpdatedAt(ZonedDateTime.now());
            orderRepository.save(order);
            
            log.info("Order {} has been cancelled", orderId);
        } else {
            throw new IllegalStateException("Cannot cancel order in current status");
        }
    }

    @Override
    @Transactional
    public void markOrderAsPaid(UUID orderId, String paymentReference) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        
        // Solo se pueden marcar como pagadas órdenes pendientes
        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new IllegalStateException("Cannot mark as paid an order not in pending status");
        }
        
        // Actualizar estado de pago
        order.setPaymentStatus(Order.PaymentStatus.PAID);
        order.setPaymentReference(paymentReference);
        order.setStatus(Order.OrderStatus.COMPLETED);
        order.setUpdatedAt(ZonedDateTime.now());
        
        // Generar código QR para las entradas
        String qrCode = qrCodeService.generateTicketQrCode(
                order.getId(), 
                order.getReservation().getShow().getId(),
                System.currentTimeMillis()
        );
        order.setQrCode(qrCode);
        
        orderRepository.save(order);
        
        log.info("Order {} has been marked as paid", orderId);
    }
    
    /**
     * Procesa los items de la orden (productos, combos)
     */
    private List<OrderItem> processOrderItems(OrderRequest orderRequest, Order order) {
        List<OrderItem> orderItems = new ArrayList<>();
        
        // Procesar products y combos si están presentes
        if (orderRequest.getItems() != null && !orderRequest.getItems().isEmpty()) {
            for (OrderRequest.OrderItemRequest itemRequest : orderRequest.getItems()) {
                OrderItem orderItem = new OrderItem();
                orderItem.setId(UUID.randomUUID());
                orderItem.setOrder(order);
                orderItem.setCreatedAt(ZonedDateTime.now());
                orderItem.setUpdatedAt(ZonedDateTime.now());
                orderItem.setQuantity(itemRequest.getQuantity());
                
                // Determinar tipo de item
                if ("PRODUCT".equals(itemRequest.getItemType())) {
                    Product product = productRepository.findById(itemRequest.getItemId())
                            .orElseThrow(() -> new ResourceNotFoundException("Product", "id", itemRequest.getItemId()));
                    
                    orderItem.setItemType(OrderItem.ItemType.PRODUCT);
                    orderItem.setItemId(product.getId());
                    orderItem.setUnitPrice(product.getPrice());
                    orderItem.setSubtotal(product.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity())));
                } else if ("COMBO".equals(itemRequest.getItemType())) {
                    Combo combo = comboRepository.findById(itemRequest.getItemId())
                            .orElseThrow(() -> new ResourceNotFoundException("Combo", "id", itemRequest.getItemId()));
                    
                    orderItem.setItemType(OrderItem.ItemType.COMBO);
                    orderItem.setItemId(combo.getId());
                    orderItem.setUnitPrice(combo.getPrice());
                    orderItem.setSubtotal(combo.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity())));
                } else {
                    throw new IllegalArgumentException("Invalid item type: " + itemRequest.getItemType());
                }
                
                orderItems.add(orderItem);
            }
        }
        
        return orderItems;
    }
    
    /**
     * Calcula el subtotal de las entradas en una reserva
     */
    private BigDecimal calculateTicketsSubtotal(Reservation reservation) {
        return reservation.getReservedSeats().stream()
                .map(ReservedSeat::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    /**
     * Calcula el subtotal de los items (productos, combos)
     */
    private BigDecimal calculateItemsSubtotal(List<OrderItem> items) {
        return items.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    /**
     * Mapea una entidad Order a un DTO OrderResponse
     */
    private OrderResponse mapToOrderResponse(Order order) {
        Reservation reservation = order.getReservation();
        Show show = reservation.getShow();
        Room room = show.getRoom();
        Cinema cinema = room.getCinema();
        Movie movie = show.getMovie();
        
        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUser() != null ? order.getUser().getId() : null)
                .reservationId(reservation.getId())
                .movieTitle(movie.getTitle())
                .showDateTime(show.getStartTime())
                .cinemaName(cinema.getName())
                .roomName(room.getName())
                .numberOfSeats(reservation.getReservedSeats().size())
                .subtotal(order.getSubtotal())
                .discount(order.getDiscount())
                .tax(order.getTax())
                .total(order.getTotal())
                .paymentMethod(order.getPaymentMethod())
                .paymentStatus(order.getPaymentStatus().name())
                .orderType(order.getOrderType().name())
                .orderStatus(order.getStatus().name())
                .createdAt(order.getCreatedAt())
                .build();
    }
    
    /**
     * Mapea una entidad Order a un DTO OrderDetailsResponse
     */
    private OrderDetailsResponse mapToOrderDetailsResponse(Order order, boolean includeQr) {
        Reservation reservation = order.getReservation();
        Show show = reservation.getShow();
        Room room = show.getRoom();
        Cinema cinema = room.getCinema();
        Movie movie = show.getMovie();
        
        // Mapear show details
        ShowResponse showResponse = ShowResponse.builder()
                .id(show.getId())
                .movieId(movie.getId())
                .movieTitle(movie.getTitle())
                .moviePosterUrl(movie.getPosterUrl())
                .roomId(room.getId())
                .roomName(room.getName())
                .roomType(room.getRoomType().name())
                .cinemaId(cinema.getId())
                .cinemaName(cinema.getName())
                .startTime(show.getStartTime())
                .endTime(show.getEndTime())
                .is3d(show.getIs3d())
                .isSubtitled(show.getIsSubtitled())
                .language(show.getLanguage())
                .status(show.getStatus().name())
                .build();
        
        // Mapear asientos reservados
        List<OrderDetailsResponse.ReservedSeatResponse> reservedSeats = reservation.getReservedSeats().stream()
                .map(rs -> OrderDetailsResponse.ReservedSeatResponse.builder()
                        .id(rs.getId())
                        .rowName(rs.getSeat().getRowName())
                        .number(rs.getSeat().getNumber())
                        .seatType(rs.getSeat().getSeatType().name())
                        .ticketType(rs.getTicketType().getName())
                        .price(rs.getPrice())
                        .build())
                .collect(Collectors.toList());
        
        // Mapear items de la orden
        List<OrderDetailsResponse.OrderItemResponse> orderItems = order.getItems().stream()
                .map(item -> {
                    String name = "";
                    
                    // Obtener nombre del item según el tipo
                    if (item.getItemType() == OrderItem.ItemType.PRODUCT) {
                        Product product = productRepository.findById(item.getItemId()).orElse(null);
                        if (product != null) {
                            name = product.getName();
                        }
                    } else if (item.getItemType() == OrderItem.ItemType.COMBO) {
                        Combo combo = comboRepository.findById(item.getItemId()).orElse(null);
                        if (combo != null) {
                            name = combo.getName();
                        }
                    }
                    
                    return OrderDetailsResponse.OrderItemResponse.builder()
                            .id(item.getId())
                            .itemType(item.getItemType().name())
                            .name(name)
                            .quantity(item.getQuantity())
                            .unitPrice(item.getUnitPrice())
                            .subtotal(item.getSubtotal())
                            .build();
                })
                .collect(Collectors.toList());
        
        // Usuario
        String userFullName = "";
        String userEmail = "";
        if (order.getUser() != null) {
            userFullName = order.getUser().getFirstName() + " " + order.getUser().getLastName();
            userEmail = order.getUser().getEmail();
        }
        
        OrderDetailsResponse response = OrderDetailsResponse.builder()
                .id(order.getId())
                .userId(order.getUser() != null ? order.getUser().getId() : null)
                .userFullName(userFullName)
                .userEmail(userEmail)
                .showDetails(showResponse)
                .reservedSeats(reservedSeats)
                .items(orderItems)
                .subtotal(order.getSubtotal())
                .discount(order.getDiscount())
                .tax(order.getTax())
                .total(order.getTotal())
                .paymentMethod(order.getPaymentMethod())
                .paymentStatus(order.getPaymentStatus().name())
                .orderType(order.getOrderType().name())
                .orderStatus(order.getStatus().name())
                .createdAt(order.getCreatedAt())
                .build();
        
        // Incluir QR si se solicita y la orden está pagada
        if (includeQr && order.getPaymentStatus() == Order.PaymentStatus.PAID) {
            response.setQrCode(order.getQrCode());
        }
        
        return response;
    }
}