package com.cinetickets.api.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieRequest {
    
    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title cannot exceed 255 characters")
    private String title;
    
    @Size(max = 2000, message = "Synopsis cannot exceed 2000 characters")
    private String synopsis;
    
    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be at least 1 minute")
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
    
    private String status; // ACTIVE, INACTIVE, UPCOMING, ARCHIVED
}