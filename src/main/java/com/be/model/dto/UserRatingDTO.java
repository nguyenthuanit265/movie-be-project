package com.be.model.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRatingDTO {
    private Long id;
    private MovieDTO movie;
    private Float rating;
    private ZonedDateTime createdAt;
}
