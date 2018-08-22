package ru.nyrk.web.flux.webflux;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.server.HandlerFilterFunction;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;


/**
 * todo:java doc
 */
public class ExampleHandlerFilterFunction implements HandlerFilterFunction<ServerResponse, ServerResponse> {
    private static final Logger logger = LoggerFactory.getLogger(ExampleHandlerFilterFunction.class);

    private String text;

    public ExampleHandlerFilterFunction(String text) {
        this.text = text;
    }

    @Override
    public Mono<ServerResponse> filter(ServerRequest serverRequest, HandlerFunction<ServerResponse> next) {
        logger.info("{}", text);
        return next.handle(serverRequest);
    }
}
