package dev.seatlock.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class EventDto {
    private Long id;
    private String name;
    private Instant startsAt;
}
