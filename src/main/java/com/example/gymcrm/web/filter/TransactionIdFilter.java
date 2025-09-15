package com.example.gymcrm.web.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

public class TransactionIdFilter implements Filter {

    public static final String TX_ID = "txId";
    public static final String TX_HEADER = "X-Transaction-Id";
    private static final Logger log = LoggerFactory.getLogger(TransactionIdFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpReq = (HttpServletRequest) request;
        HttpServletResponse httpRes = (HttpServletResponse) response;

        String incoming = httpReq.getHeader(TX_HEADER);
        String txId = (incoming == null || incoming.isBlank()) ? UUID.randomUUID().toString() : incoming;

        MDC.put(TX_ID, txId);
        httpRes.setHeader(TX_HEADER, txId);

        String uri = httpReq.getRequestURI();
        String qs = Optional.ofNullable(httpReq.getQueryString()).map(q -> "?" + q).orElse("");
        long start = System.currentTimeMillis();

        StatusCapturingResponseWrapper wrapper = new StatusCapturingResponseWrapper(httpRes);
        try {
            log.info("→ {} {}{}", httpReq.getMethod(), uri, qs);
            chain.doFilter(request, wrapper);
        } finally {
            long tookMs = System.currentTimeMillis() - start;
            log.info("← {} {}{} status={} took={}ms",
                    httpReq.getMethod(), uri, qs, wrapper.getStatus(), tookMs);
            MDC.remove(TX_ID);
        }
    }

    private static class StatusCapturingResponseWrapper extends HttpServletResponseWrapper {
        private int httpStatus = SC_OK;

        StatusCapturingResponseWrapper(HttpServletResponse response) {
            super(response);
        }

        @Override
        public void setStatus(int sc) {
            super.setStatus(sc);
            this.httpStatus = sc;
        }

        @Override
        public void sendError(int sc) throws IOException {
            super.sendError(sc);
            this.httpStatus = sc;
        }

        @Override
        public void sendError(int sc, String msg) throws IOException {
            super.sendError(sc, msg);
            this.httpStatus = sc;
        }

        @Override
        public int getStatus() {
            return httpStatus;
        }
    }
}
