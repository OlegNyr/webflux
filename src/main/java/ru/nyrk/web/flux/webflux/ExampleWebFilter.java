package ru.nyrk.web.flux.webflux;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class ExampleWebFilter implements WebFilter {
    private static final Logger logger = LoggerFactory.getLogger(ExampleWebFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        exchange.getResponse().getHeaders().add("web-filter", "web-filter-test");
        exchange.getResponse().beforeCommit(() -> {
            exchange.getResponse().getHeaders().add("uuid", UUID.randomUUID().toString());
            return Mono.empty();
        });
        return chain.filter(exchange);
    }
}
