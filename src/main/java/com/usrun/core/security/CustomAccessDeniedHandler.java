package com.usrun.core.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.usrun.core.config.ErrorCode;
import com.usrun.core.payload.CodeResponse;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

/**
 * @author phuctt4
 */

@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

  @Override
  public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
      AccessDeniedException e) throws IOException, ServletException {

    httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
    httpServletResponse.setContentType("application/json");

    ObjectMapper objectMapper = new ObjectMapper();

    String error = objectMapper
        .writeValueAsString(new CodeResponse(ErrorCode.USER_DOES_NOT_PERMISSION));

    httpServletResponse.getOutputStream().println(error);
  }
}
