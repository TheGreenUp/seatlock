package dev.seatlock.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.Instant;

@Data
@Entity
@Table(name = "events")
public class Event {
    @Id
    private Long id;
    private String name;
    private Instant startsAt;
}
