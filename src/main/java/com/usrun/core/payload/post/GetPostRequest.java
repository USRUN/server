package com.usrun.core.payload.post;

import lombok.Getter;
import lombok.Setter;

/**
 * @author phuctt4
 */

@Getter
@Setter
public class GetPostRequest {
    private long teamId;
    private long postId;
}
