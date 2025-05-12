package com.cinetickets.api.service.impl;

import com.cinetickets.api.dto.response.*;
import com.cinetickets.api.entity.Order;
import com.cinetickets.api.entity.OrderItem;
import com.cinetickets.api.repository.*;
import com.cinetickets.api.service.StatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final OrderRepository orderRepository;
    private final ReservationRepository reservationRepository;
    private final ShowRepository showRepository;
    private final MovieRepository movieRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ComboRepository comboRepository;

    private final UUID defaultCinemaId = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6");

    @Override
    @Transactional(readOnly = true)
    public SalesStatsResponse getSalesStats(LocalDate startDate, LocalDate endDate) {
        // Convertir LocalDate a ZonedDateTime para consulta
        ZonedDateTime startDateTime = startDate.atStartOfDay(ZoneId.systemDefault());
        ZonedDateTime endDateTime = endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault());
        
        // Obtener órdenes completadas y pagadas en el período
        List<Order> orders = orderRepository.findCompletedOrdersInDateRange(startDateTime, endDateTime);
        
        // Calcular totales
        BigDecimal totalSales = BigDecimal.ZERO;
        BigDecimal ticketSales = BigDecimal.ZERO;
        BigDecimal productSales = BigDecimal.ZERO;
        
        // Acumular ventas por día
        Map<LocalDate, SalesStatsResponse.DailySalesData> dailySalesMap = new HashMap<>();
        
        for (Order order : orders) {
            totalSales = totalSales.add(order.getTotal());
            
            // Clasificar ventas por tipo
            for (OrderItem item : order.getItems()) {
                if (item.getItemType() == OrderItem.ItemType.TICKET) {
                    ticketSales = ticketSales.add(item.getSubtotal());
                } else {
                    productSales = productSales.add(item.getSubtotal());
                }
            }
            
            // Acumular por día
            LocalDate orderDate = order.getCreatedAt().toLocalDate();
            SalesStatsResponse.DailySalesData dailyData = dailySalesMap.getOrDefault(orderDate,
                    SalesStatsResponse.DailySalesData.builder()
                            .date(orderDate)
                            .totalSales(BigDecimal.ZERO)
                            .transactions(0)
                            .build());
            
            dailyData.setTotalSales(dailyData.getTotalSales().add(order.getTotal()));
            dailyData.setTransactions(dailyData.getTransactions() + 1);
            
            dailySalesMap.put(orderDate, dailyData);
        }
        
        // Calcular promedios
        BigDecimal averageTicketPrice = orders.isEmpty() ? BigDecimal.ZERO :
                ticketSales.divide(BigDecimal.valueOf(orders.size()), 2, RoundingMode.HALF_UP);
        
        BigDecimal averageTransactionValue = orders.isEmpty() ? BigDecimal.ZERO :
                totalSales.divide(BigDecimal.valueOf(orders.size()), 2, RoundingMode.HALF_UP);
        
        // Ordenar ventas diarias por fecha
        List<SalesStatsResponse.DailySalesData> dailySales = dailySalesMap.values().stream()
                .sorted(Comparator.comparing(SalesStatsResponse.DailySalesData::getDate))
                .collect(Collectors.toList());
        
        return SalesStatsResponse.builder()
                .totalSales(totalSales)
                .ticketSales(ticketSales)
                .productSales(productSales)
                .averageTicketPrice(averageTicketPrice)
                .averageTransactionValue(averageTransactionValue)
                .totalTransactions(orders.size())
                .dailySales(dailySales)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AttendanceStatsResponse getAttendanceStats(LocalDate startDate, LocalDate endDate) {
        // Implementación simplificada para demostración
        return AttendanceStatsResponse.builder()
                .totalAttendance(1250)
                .averageOccupancyRate(0.75)
                .totalShows(48)
                .attendanceByRoom(Map.of(
                        "Sala 1", 320,
                        "Sala 2", 420,
                        "Sala 3", 510
                ))
                .dailyAttendance(List.of(
                        AttendanceStatsResponse.DailyAttendanceData.builder()
                                .date(startDate)
                                .attendance(150)
                                .shows(6)
                                .occupancyRate(0.68)
                                .build(),
                        AttendanceStatsResponse.DailyAttendanceData.builder()
                                .date(startDate.plusDays(1))
                                .attendance(180)
                                .shows(6)
                                .occupancyRate(0.82)
                                .build()
                ))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovieStatsResponse> getTopMoviesStats(LocalDate startDate, LocalDate endDate, int limit) {
        // Implementación simplificada para demostración
        return List.of(
                MovieStatsResponse.builder()
                        .movieId(UUID.randomUUID())
                        .title("Avengers: Endgame")
                        .attendance(320)
                        .shows(12)
                        .sales(new BigDecimal("38400.00"))
                        .occupancyRate(0.85)
                        .build(),
                MovieStatsResponse.builder()
                        .movieId(UUID.randomUUID())
                        .title("The Lion King")
                        .attendance(290)
                        .shows(10)
                        .sales(new BigDecimal("33350.00"))
                        .occupancyRate(0.78)
                        .build()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductStatsResponse> getTopProductsStats(LocalDate startDate, LocalDate endDate, int limit) {
        // Implementación simplificada para demostración
        return List.of(
                ProductStatsResponse.builder()
                        .productId(UUID.randomUUID())
                        .name("Combo Grande")
                        .type("COMBO")
                        .quantity(156)
                        .sales(new BigDecimal("18720.00"))
                        .build(),
                ProductStatsResponse.builder()
                        .productId(UUID.randomUUID())
                        .name("Pochos Grande")
                        .type("PRODUCT")
                        .quantity(132)
                        .sales(new BigDecimal("10560.00"))
                        .build()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ConversionStatsResponse getConversionStats(LocalDate startDate, LocalDate endDate) {
        // Implementación simplificada para demostración
        return ConversionStatsResponse.builder()
                .reservations(320)
                .completedOrders(280)
                .conversionRate(0.875)
                .abandonment(40)
                .abandonmentRate(0.125)
                .lostRevenue(new BigDecimal("4800.00"))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public TimeAnalysisResponse getTimeAnalysis(LocalDate startDate, LocalDate endDate) {
        // Implementación simplificada para demostración
        return TimeAnalysisResponse.builder()
                .attendanceByDayOfWeek(Map.of(
                        DayOfWeek.FRIDAY, 310,
                        DayOfWeek.SATURDAY, 420,
                        DayOfWeek.SUNDAY, 380
                ))
                .attendanceByHour(Map.of(
                        14, 120,
                        17, 180,
                        20, 350,
                        22, 280
                ))
                .peakTimes(List.of(
                        TimeAnalysisResponse.PeakTimeData.builder()
                                .dayOfWeek(DayOfWeek.SATURDAY)
                                .startTime(LocalTime.of(20, 0))
                                .attendance(95)
                                .occupancyRate(0.95)
                                .build()
                ))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserStatsResponse> getTopUserStats(LocalDate startDate, LocalDate endDate, int limit) {
        // Implementación simplificada para demostración
        return List.of(
                UserStatsResponse.builder()
                        .userId(UUID.randomUUID())
                        .name("Juan Pérez")
                        .email("juan.perez@example.com")
                        .visits(12)
                        .totalSpent(new BigDecimal("15600.00"))
                        .loyaltyPoints(320)
                        .lastVisit(LocalDate.now().minusDays(3))
                        .build(),
                UserStatsResponse.builder()
                        .userId(UUID.randomUUID())
                        .name("Ana López")
                        .email("ana.lopez@example.com")
                        .visits(9)
                        .totalSpent(new BigDecimal("12800.00"))
                        .loyaltyPoints(280)
                        .lastVisit(LocalDate.now().minusDays(5))
                        .build()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExportDataResponse> getExportData(LocalDate startDate, LocalDate endDate, String type) {
        // Implementación simplificada para demostración
        List<ExportDataResponse> result = new ArrayList<>();
        
        // Devolver diferentes datos según el tipo solicitado
        if ("sales".equals(type)) {
            Map<String, Object> data1 = new HashMap<>();
            data1.put("date", "2023-04-01");
            data1.put("movie", "Avengers: Endgame");
            data1.put("tickets", 85);
            data1.put("revenue", 10200.00);
            
            Map<String, Object> data2 = new HashMap<>();
            data2.put("date", "2023-04-01");
            data2.put("movie", "The Lion King");
            data2.put("tickets", 72);
            data2.put("revenue", 8640.00);
            
            result.add(new ExportDataResponse(data1));
            result.add(new ExportDataResponse(data2));
        } else if ("products".equals(type)) {
            Map<String, Object> data1 = new HashMap<>();
            data1.put("date", "2023-04-01");
            data1.put("product", "Combo Grande");
            data1.put("quantity", 45);
            data1.put("revenue", 5400.00);
            
            Map<String, Object> data2 = new HashMap<>();
            data2.put("date", "2023-04-01");
            data2.put("product", "Pochoclos Grande");
            data2.put("quantity", 38);
            data2.put("revenue", 3040.00);
            
            result.add(new ExportDataResponse(data1));
            result.add(new ExportDataResponse(data2));
        } else if ("users".equals(type)) {
            Map<String, Object> data1 = new HashMap<>();
            data1.put("email", "juan.perez@example.com");
            data1.put("name", "Juan Pérez");
            data1.put("visits", 12);
            data1.put("totalSpent", 15600.00);
            data1.put("loyaltyPoints", 320);
            
            Map<String, Object> data2 = new HashMap<>();
            data2.put("email", "ana.lopez@example.com");
            data2.put("name", "Ana López");
            data2.put("visits", 9);
            data2.put("totalSpent", 12800.00);
            data2.put("loyaltyPoints", 280);
            
            result.add(new ExportDataResponse(data1));
            result.add(new ExportDataResponse(data2));
        }
        
        return result;
    }
}