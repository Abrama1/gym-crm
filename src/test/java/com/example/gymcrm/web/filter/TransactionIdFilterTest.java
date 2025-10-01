package com.example.gymcrm.web.filter;

import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;

class TransactionIdFilterTest {

    private final TransactionIdFilter filter = new TransactionIdFilter();

    @Test
    void setsHeaderAndCleansMdc_whenNoIncomingHeader() throws Exception {
        var req = new MockHttpServletRequest("GET", "/api/ping");
        var res = new MockHttpServletResponse();
        var chain = new MockFilterChain();

        assertNull(MDC.get(TransactionIdFilter.TX_ID));

        filter.doFilter(req, res, chain);

        var outHeader = res.getHeader(TransactionIdFilter.TX_HEADER);
        assertNotNull(outHeader);
        assertFalse(outHeader.toString().isBlank());

        // MDC must be cleared after request
        assertNull(MDC.get(TransactionIdFilter.TX_ID));
    }

    @Test
    void preservesIncomingHeader() throws Exception {
        var req = new MockHttpServletRequest("GET", "/api/ping");
        req.addHeader(TransactionIdFilter.TX_HEADER, "abc-123");

        var res = new MockHttpServletResponse();
        var chain = new MockFilterChain();

        filter.doFilter(req, res, chain);

        assertEquals("abc-123", res.getHeader(TransactionIdFilter.TX_HEADER));
        assertNull(MDC.get(TransactionIdFilter.TX_ID));
    }
}
