package com.sgi.bootcoin.Infrastructure.subscriber.events;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface EventHandle {

    @JsonProperty("@topic")
    default String getTopic() {
        return this.getClass().getSimpleName();
    }
}
