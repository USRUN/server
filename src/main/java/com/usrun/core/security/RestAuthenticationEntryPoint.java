package com.usrun.core.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.usrun.core.config.ErrorCode;
import com.usrun.core.payload.CodeResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private static final Logger logger = LoggerFactory.getLogger(RestAuthenticationEntryPoint.class);

    @Override
    public void commence(HttpServletRequest httpServletRequest,
                         HttpServletResponse httpServletResponse,
                         AuthenticationException e) throws IOException {

        logger.error("Responding with unauthorized error. Message - {}", e.getMessage());

        httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        httpServletResponse.setContentType("application/json");

        ObjectMapper objectMapper = new ObjectMapper();

        String error = objectMapper
                .writeValueAsString(new CodeResponse(ErrorCode.USER_DOES_NOT_PERMISSION));

        httpServletResponse.getOutputStream().println(error);
    }
}
