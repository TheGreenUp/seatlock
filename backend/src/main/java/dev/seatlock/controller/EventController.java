package dev.seatlock.controller;

import dev.seatlock.dto.EventDto;
import dev.seatlock.service.EventService;
import dev.seatlock.util.EventMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping("/events")
    public List<EventDto> fetchEvents() {
        return eventService.fetchEvents().stream()
                .map(EventMapper::toDto)
                .toList();
    }
}
