package dev.seatlock.util;

import dev.seatlock.dto.EventDto;
import dev.seatlock.entity.Event;

public final class EventMapper {

    private EventMapper() {
    }

    public static EventDto toDto(Event event) {
        EventDto dto = new EventDto();
        dto.setId(event.getId());
        dto.setName(event.getName());
        dto.setStartsAt(event.getStartsAt());
        return dto;
    }

    public static Event toEntity(EventDto dto) {
        Event event = new Event();
        event.setId(dto.getId());
        event.setName(dto.getName());
        event.setStartsAt(dto.getStartsAt());
        return event;
    }
}
