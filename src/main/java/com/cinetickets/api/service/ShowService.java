package com.cinetickets.api.service;

import com.cinetickets.api.dto.request.ShowRequest;
import com.cinetickets.api.dto.response.SeatResponse;
import com.cinetickets.api.dto.response.ShowResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ShowService {
    
    List<ShowResponse> getShowsForMovie(UUID movieId, String date);
    
    List<ShowResponse> getShowsForMovieInCinema(UUID movieId, UUID cinemaId, String date);
    
    ShowResponse getShowById(UUID id);
    
    List<SeatResponse> getAvailableSeatsForShow(UUID showId);
    
    List<ShowResponse> getActiveShowsForCinema(UUID cinemaId, String date);
    
    List<ShowResponse> getCurrentlyPlayingShows();
    
    Page<ShowResponse> getAllShowsByFilter(UUID cinemaId, UUID movieId, String date, Pageable pageable);
    
    UUID createShow(ShowRequest showRequest);
    
    void updateShow(UUID id, ShowRequest showRequest);
    
    void cancelShow(UUID id);
    
    void deleteShow(UUID id);
}