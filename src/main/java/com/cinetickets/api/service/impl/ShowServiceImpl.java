package com.cinetickets.api.service.impl;

import com.cinetickets.api.dto.request.ShowRequest;
import com.cinetickets.api.dto.response.SeatResponse;
import com.cinetickets.api.dto.response.ShowResponse;
import com.cinetickets.api.entity.Movie;
import com.cinetickets.api.entity.Room;
import com.cinetickets.api.entity.Show;
import com.cinetickets.api.entity.Seat;
import com.cinetickets.api.exception.ResourceNotFoundException;
import com.cinetickets.api.repository.MovieRepository;
import com.cinetickets.api.repository.RoomRepository;
import com.cinetickets.api.repository.ShowRepository;
import com.cinetickets.api.repository.SeatRepository;
import com.cinetickets.api.service.ShowService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShowServiceImpl implements ShowService {

    private final ShowRepository showRepository;
    private final MovieRepository movieRepository;
    private final RoomRepository roomRepository;
    private final SeatRepository seatRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ShowResponse> getShowsForMovie(UUID movieId, String date) {
        ZonedDateTime startDate = parseDate(date);
        ZonedDateTime endDate = startDate.plusDays(1);
        
        List<Show> shows = showRepository.findByMovieIdAndStartTimeGreaterThanEqualOrderByStartTime(
            movieId, startDate
        );
        
        return shows.stream()
                .map(this::mapToShowResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShowResponse> getShowsForMovieInCinema(UUID movieId, UUID cinemaId, String date) {
        ZonedDateTime startDate = parseDate(date);
        ZonedDateTime endDate = startDate.plusDays(1);
        
        // Implementar lógica para obtener funciones de una película en un cine específico
        // Puedes necesitar agregar un método personalizado en ShowRepository
        return List.of(); // Placeholder
    }

    @Override
    @Transactional(readOnly = true)
    public ShowResponse getShowById(UUID id) {
        Show show = showRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Show", "id", id));
        
        return mapToShowResponse(show);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SeatResponse> getAvailableSeatsForShow(UUID showId) {
        // Obtener todos los asientos de la sala de la función
        Show show = showRepository.findById(showId)
                .orElseThrow(() -> new ResourceNotFoundException("Show", "id", showId));
        
        // Obtener asientos reservados para esta función
        List<UUID> reservedSeatIds = seatRepository.findReservedSeatIdsForShow(showId);
        
        return show.getRoom().getSeats().stream()
                .map(seat -> SeatResponse.builder()
                        .id(seat.getId())
                        .row(seat.getRow())
                        .number(seat.getNumber())
                        .seatType(seat.getSeatType().name())
                        .status(seat.getStatus().name())
                        .isAvailable(!reservedSeatIds.contains(seat.getId()))
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShowResponse> getActiveShowsForCinema(UUID cinemaId, String date) {
        ZonedDateTime startDate = parseDate(date);
        ZonedDateTime endDate = startDate.plusDays(1);
        
        List<Show> shows = showRepository.findAllActiveInCinemaForDateRange(cinemaId, startDate, endDate);
        
        return shows.stream()
                .map(this::mapToShowResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShowResponse> getCurrentlyPlayingShows() {
        return showRepository.findAllShowsCurrentlyPlaying().stream()
                .map(this::mapToShowResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ShowResponse> getAllShowsByFilter(UUID cinemaId, UUID movieId, String date, Pageable pageable) {
        // Implementación de filtrado de funciones
        // Puedes necesitar agregar un método personalizado en ShowRepository
        return Page.empty(); // Placeholder
    }

    @Override
    @Transactional
    public UUID createShow(ShowRequest showRequest) {
        Movie movie = movieRepository.findById(showRequest.getMovieId())
                .orElseThrow(() -> new ResourceNotFoundException("Movie", "id", showRequest.getMovieId()));
        
        Room room = roomRepository.findById(showRequest.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room", "id", showRequest.getRoomId()));
        
        Show show = Show.builder()
                .id(UUID.randomUUID())
                .movie(movie)
                .room(room)
                .startTime(showRequest.getStartTime())
                .endTime(showRequest.getStartTime().plusMinutes(movie.getDurationMinutes()))
                .is3d(showRequest.getIs3d() != null ? showRequest.getIs3d() : movie.getIs3d())
                .isSubtitled(showRequest.getIsSubtitled() != null ? showRequest.getIsSubtitled() : movie.getIsSubtitled())
                .language(showRequest.getLanguage() != null ? showRequest.getLanguage() : movie.getLanguage())
                .status(Show.ShowStatus.SCHEDULED)
                .build();
        
        Show savedShow = showRepository.save(show);
        return savedShow.getId();
    }

    @Override
    @Transactional
    public void updateShow(UUID id, ShowRequest showRequest) {
        Show show = showRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Show", "id", id));
        
        Movie movie = movieRepository.findById(showRequest.getMovieId())
                .orElseThrow(() -> new ResourceNotFoundException("Movie", "id", showRequest.getMovieId()));
        
        Room room = roomRepository.findById(showRequest.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room", "id", showRequest.getRoomId()));
        
        show.setMovie(movie);
        show.setRoom(room);
        show.setStartTime(showRequest.getStartTime());
        show.setEndTime(showRequest.getStartTime().plusMinutes(movie.getDurationMinutes()));
        show.setIs3d(showRequest.getIs3d() != null ? showRequest.getIs3d() : movie.getIs3d());
        show.setIsSubtitled(showRequest.getIsSubtitled() != null ? showRequest.getIsSubtitled() : movie.getIsSubtitled());
        show.setLanguage(showRequest.getLanguage() != null ? showRequest.getLanguage() : movie.getLanguage());
        
        showRepository.save(show);
    }

    @Override
    @Transactional
    public void cancelShow(UUID id) {
        Show show = showRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Show", "id", id));
        
        show.setStatus(Show.ShowStatus.CANCELED);
        showRepository.save(show);
    }

    @Override
    @Transactional
    public void deleteShow(UUID id) {
        Show show = showRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Show", "id", id));
        
        showRepository.delete(show);
    }

    // Mapeo de Show a ShowResponse
    private ShowResponse mapToShowResponse(Show show) {
        return ShowResponse.builder()
                .id(show.getId())
                .movieId(show.getMovie().getId())
                .movieTitle(show.getMovie().getTitle())
                .moviePosterUrl(show.getMovie().getPosterUrl())
                .roomId(show.getRoom().getId())
                .roomName(show.getRoom().getName())
                .roomType(show.getRoom().getRoomType().name())
                .cinemaId(show.getRoom().getCinema().getId())
                .cinemaName(show.getRoom().getCinema().getName())
                .startTime(show.getStartTime())
                .endTime(show.getEndTime())
                .is3d(show.getIs3d())
                .isSubtitled(show.getIsSubtitled())
                .language(show.getLanguage())
                .status(show.getStatus().name())
                .build();
    }

    // Método auxiliar para parsear la fecha
    private ZonedDateTime parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return ZonedDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        }
        
        LocalDate localDate = LocalDate.parse(dateStr);
        return localDate.atStartOfDay(ZoneId.systemDefault());
    }
}