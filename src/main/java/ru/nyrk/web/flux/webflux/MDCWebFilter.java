package ru.nyrk.web.flux.webflux;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.util.Map;
import java.util.UUID;

public class MDCWebFilter implements WebFilter {
    private static final Logger logger = LoggerFactory.getLogger(MDCWebFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        HttpHeaders headers = exchange.getRequest().getHeaders();
        MDC.put("xuuid", headers.getFirst("x-uuid"));
        return chain.filter(exchange)
                .doOnSuccessOrError((a, t) -> {
                    logger.info("Clear MDC {}", MDC.getCopyOfContextMap());
                    MDC.clear();
                })
                .subscriberContext(Context.of(Map.class, MDC.getCopyOfContextMap()));
    }
}
