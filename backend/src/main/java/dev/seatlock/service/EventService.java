package dev.seatlock.service;

import dev.seatlock.entity.Event;
import dev.seatlock.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    public List<Event> fetchEvents() {
        return eventRepository.findAll(Sort.by(Sort.Direction.ASC, "startsAt", "id"));
    }

}
