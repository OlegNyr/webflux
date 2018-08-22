package ru.nyrk.web.flux.webflux.log;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;

/**
 * todo:java doc
 */
public interface LogMessageFormatter {
    String format(ServerHttpRequest request, ServerHttpResponse response, byte[] payload);
}
