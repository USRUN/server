package com.usrun.core.payload.post;

import lombok.Getter;
import lombok.Setter;

/**
 * @author phuctt4
 */

@Getter
@Setter
public class GetPostsRequest {
    private long teamId;
    private int count = 30;
    private int offset = 0;
}
