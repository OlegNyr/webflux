package ru.nyrk.web.flux.webflux.log;

import org.springframework.http.MediaType;

/**
 * todo:java doc
 */
public interface MediaTypeFilter {
    default boolean logged(MediaType mediaType) {
        return true;
    }
}
