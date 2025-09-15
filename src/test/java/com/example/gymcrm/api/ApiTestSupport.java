package com.example.gymcrm.api;

import com.example.gymcrm.api.RestExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

abstract class ApiTestSupport {

    protected MockMvc mockMvc;
    protected final ObjectMapper om = new ObjectMapper().registerModule(new JavaTimeModule());
    protected final MappingJackson2HttpMessageConverter json =
            new MappingJackson2HttpMessageConverter(om);

    protected MockMvc build(Object controller) {
        return MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new RestExceptionHandler())
                .setMessageConverters(json)
                .build();
    }

    @BeforeEach
    void noop() { /* subclass builds its own MockMvc in @BeforeEach */ }
}
