package com.example.gymcrm.config;

import com.example.gymcrm.web.filter.TransactionIdFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<TransactionIdFilter> txIdFilter() {
        FilterRegistrationBean<TransactionIdFilter> frb =
                new FilterRegistrationBean<>(new TransactionIdFilter());
        frb.setOrder(Ordered.HIGHEST_PRECEDENCE);
        frb.addUrlPatterns("/*");
        return frb;
    }
}
