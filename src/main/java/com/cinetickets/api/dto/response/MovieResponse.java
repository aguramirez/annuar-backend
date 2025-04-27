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
public class MovieResponse {
    
    private UUID id;
    private String title;
    private String genre;
    private Integer durationMinutes;
    private String rating;
    private String posterUrl;
    private LocalDate releaseDate;
    private Boolean is3d;
    private Boolean isSubtitled;
    private String language;
    private String status;
}