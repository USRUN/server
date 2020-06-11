package com.usrun.core.payload.track;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author phuctt4
 */
@Getter
@Setter
public class GetTrackRequest {
    private long trackId;
}
