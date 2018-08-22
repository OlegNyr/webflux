package ru.nyrk.web.flux;

import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

import static com.google.common.collect.ImmutableList.of;

/**
 * todo:java doc
 */
public class MonoJust {
    @Test
    public void just() {
        String text = "tttt";
        Assert.assertEquals(Mono.justOrEmpty(Optional.ofNullable(text)).block(), text);

    }

    @Test
    public void ifEmpty() {
        String text = null;
        String str = Mono.justOrEmpty(Optional.ofNullable(text)).switchIfEmpty(Mono.just("str")).block();
        Assert.assertEquals(str, "str");
    }

    @Test
    public void next() {
        List<String> texts = of("1", "2");
        Flux
                .fromIterable(texts)
                .next()
                .switchIfEmpty(Mono.just("str"))
                .doOnNext(System.out::println)
                .block();
    }
}
