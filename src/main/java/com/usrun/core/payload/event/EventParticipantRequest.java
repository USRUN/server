package com.usrun.core.payload.event;

import lombok.Data;

@Data
public class EventParticipantRequest {
    private long eventId;
    private int offset;
    private int count;
}
