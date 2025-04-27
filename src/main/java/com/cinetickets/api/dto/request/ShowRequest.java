package com.cinetickets.api.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShowRequest {
    
    @NotNull(message = "Movie ID is required")
    private UUID movieId;
    
    @NotNull(message = "Room ID is required")
    private UUID roomId;
    
    @NotNull(message = "Start time is required")
    @Future(message = "Start time must be in the future")
    private ZonedDateTime startTime;
    
    private Boolean is3d;
    
    private Boolean isSubtitled;
    
    private String language;
    
    private String status;
}