package com.cinetickets.api.dto.response;

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
public class ShowResponse {
    
    private UUID id;
    private UUID movieId;
    private String movieTitle;
    private String moviePosterUrl;
    private UUID roomId;
    private String roomName;
    private String roomType;
    private UUID cinemaId;
    private String cinemaName;
    private ZonedDateTime startTime;
    private ZonedDateTime endTime;
    private Boolean is3d;
    private Boolean isSubtitled;
    private String language;
    private String status;
}