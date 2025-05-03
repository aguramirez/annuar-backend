package com.cinetickets.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieDetailResponse {
    
    private UUID id;
    private String title;
    private String synopsis;
    private Integer durationMinutes;
    private LocalDate releaseDate;
    private LocalDate endDate;
    private String director;
    private String cast;
    private String genre;
    private String rating;
    private String posterUrl;
    private String trailerUrl;
    private String language;
    private Boolean is3d;
    private Boolean isSubtitled;
    private String status;
}