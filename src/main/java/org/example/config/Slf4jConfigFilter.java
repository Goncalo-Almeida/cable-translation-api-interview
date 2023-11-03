package org.example.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@Component
@Slf4j
public class Slf4jConfigFilter extends GenericFilterBean {

    private static final String TRACE_ID_KEY = "X-Trace-Id";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        try {
            String appTraceId = ((HttpServletRequest) request).getHeader(TRACE_ID_KEY);
            MDC.put(TRACE_ID_KEY, appTraceId);
            chain.doFilter(request, response);

        } finally {
            MDC.remove(TRACE_ID_KEY);
        }
    }
}
