// package com.cinetickets.api.job;

// import com.cinetickets.api.repository.ReservationRepository;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.scheduling.annotation.Scheduled;
// import org.springframework.stereotype.Component;
// import org.springframework.transaction.annotation.Transactional;

// import java.time.ZonedDateTime;

// @Slf4j
// @Component
// @RequiredArgsConstructor
// public class ReservationExpirationJob {

//     private final ReservationRepository reservationRepository;

//     /**
//      * Trabajo programado que se ejecuta cada minuto para expirar reservas que hayan superado su tiempo de expiración
//      */
//     @Scheduled(fixedRate = 60000) // Ejecutar cada minuto
//     @Transactional
//     public void expireReservations() {
//         ZonedDateTime now = ZonedDateTime.now();
//         log.debug("Executing reservation expiration job at {}", now);
        
//         int expiredCount = reservationRepository.expireReservations(now);
        
//         if (expiredCount > 0) {
//             log.info("Expired {} reservations", expiredCount);
//         }
//     }
// }