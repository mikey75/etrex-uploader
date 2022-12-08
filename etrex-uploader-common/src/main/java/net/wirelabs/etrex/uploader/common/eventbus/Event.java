package net.wirelabs.etrex.uploader.common.eventbus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Event {
    private final Object eventType;
    private final Object payload;
}
