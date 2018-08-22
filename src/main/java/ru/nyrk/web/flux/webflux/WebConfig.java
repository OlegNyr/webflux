package ru.nyrk.web.flux.webflux;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.reactive.accept.RequestedContentTypeResolverBuilder;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.PathMatchConfigurer;
import org.springframework.web.reactive.config.ViewResolverRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer;
import ru.nyrk.web.flux.webflux.log.RequestLoggingWebFilter;
import ru.nyrk.web.flux.webflux.log.ResponseLoggingWebFilter;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;

@Configuration
@EnableWebFlux
public class WebConfig implements WebFluxConfigurer {
    private static final Logger loggerReq = LoggerFactory.getLogger("ru.request");
    private static final Logger loggerResp = LoggerFactory.getLogger("ru.response");

    @Bean
    TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(10);
        return threadPoolTaskScheduler;
    }

    @Order(Ordered.LOWEST_PRECEDENCE - 100)
    @Bean
    RequestLoggingWebFilter requestLoggingWebFilter() {
        return new RequestLoggingWebFilter(loggerReq);
    }
//
    @Order(Ordered.LOWEST_PRECEDENCE - 100)
    @Bean
    ResponseLoggingWebFilter responseLoggingWebFilter() {
        return new ResponseLoggingWebFilter(loggerResp);
    }

    @Order(Ordered.HIGHEST_PRECEDENCE)
    @Bean
    MDCWebFilter mdcWebFilter() {
        return new MDCWebFilter();
    }


    @Bean
    public RouterFunction<ServerResponse> route(PlayerHandler playerHandler) {
        return RouterFunctions
                .route(GET("/players/{name}"), playerHandler::getName)
                .filter(new ExampleHandlerFilterFunction("filter1")
                                .andThen(new ExampleHandlerFilterFunction("filter2")));
    }

    @Override
    public void configureContentTypeResolver(RequestedContentTypeResolverBuilder builder) {
    }

    @Override
    public void configurePathMatching(PathMatchConfigurer configurer) {

    }

    @Override
    public void configureArgumentResolvers(ArgumentResolverConfigurer configurer) {
    }

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {

    }
}
