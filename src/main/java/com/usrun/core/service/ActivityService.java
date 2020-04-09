package com.usrun.core.service;

import com.google.common.hash.Hashing;
import com.usrun.core.config.AppProperties;
import com.usrun.core.repository.PointRepository;
import com.usrun.core.repository.TrackRepository;
import com.usrun.core.utility.CacheClient;
import com.usrun.core.utility.UniqueIDGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class ActivityService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrackService.class);

    @Autowired
    private AppProperties appProperties;
    public String getSigActivity(Long recordId) {
        System.out.println(recordId);
        StringBuffer buffer = new StringBuffer(Long.toString(recordId));
        return Hashing
                .hmacSha256(appProperties.getActivity().getKey().getBytes())
                .hashString(buffer.toString(), StandardCharsets.UTF_8)
                .toString();
    }

}
