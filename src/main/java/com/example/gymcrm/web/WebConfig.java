package com.example.gymcrm.web;

import com.example.gymcrm.web.auth.AuthInterceptor;
import com.example.gymcrm.web.filter.TransactionIdFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;

    public WebConfig(AuthInterceptor authInterceptor) {
        this.authInterceptor = authInterceptor;
    }

    @Bean
    public MappingJackson2HttpMessageConverter jacksonConverter() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return new MappingJackson2HttpMessageConverter(mapper);
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(jacksonConverter());
    }

    @Bean
    public LocalValidatorFactoryBean validator() { return new LocalValidatorFactoryBean(); }

    @Override
    public Validator getValidator() { return validator(); }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/trainees/register",
                        "/api/trainers/register",
                        "/api/auth/**",
                        "/swagger-ui/**", "/v3/**",
                        "/actuator/**"
                );
    }

    @Bean
    public FilterRegistrationBean<TransactionIdFilter> transactionIdFilter() {
        FilterRegistrationBean<TransactionIdFilter> reg = new FilterRegistrationBean<>();
        reg.setFilter(new TransactionIdFilter());
        reg.addUrlPatterns("/*");
        reg.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return reg;
    }
}
