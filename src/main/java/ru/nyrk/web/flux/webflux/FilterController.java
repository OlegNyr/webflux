package ru.nyrk.web.flux.webflux;

import org.apache.commons.lang3.RandomStringUtils;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Signal;
import reactor.core.publisher.UnicastProcessor;
import reactor.util.context.Context;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.time.Duration;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

@RestController
public class FilterController {

    @GetMapping(path = "/users/{size}")
    public Mono<String> getName(@PathVariable Long size) {
//        return Mono.just("gggg");
        logger.info("start controller");
        return Mono.fromDirect(registerCallBack(size))
//                .timeout(Duration.ofSeconds(10l), Mono.just("seee"))
                .doOnNext(this::log);
    }

    private void log(String s) {
        logger.info(s);
    }

    private static final Logger logger = LoggerFactory.getLogger(FilterController.class);

    @Autowired
    private TaskScheduler taskScheduler;

    private Publisher<String> registerCallBack(long size) {
        UnicastProcessor<String> processor = UnicastProcessor.create();
        FluxSink<String> sink = processor.sink(FluxSink.OverflowStrategy.LATEST);
        Map<String, String> copyOfContextMap = MDC.getCopyOfContextMap();
        taskScheduler.schedule(() -> {
            MDC.setContextMap(copyOfContextMap);
            logger.info("scheduler complete");
            sink.next(RandomStringUtils.randomAlphanumeric(1000)).complete();
            logger.info("scheduler complete start");
            sleep(10000L);
            logger.info("scheduler complete end");
            MDC.clear();
        }, new Date(System.currentTimeMillis() + size));
        return processor.next().doOnNext(this::log);
    }

    private void sleep(long l) {
        try {
            Thread.sleep(l);
        } catch (InterruptedException e) {

        }
    }

    static final String HTTP_CORRELATION_ID = "reactive.http.library.correlationId";

    Mono<Tuple2<Integer, String>> doPut(String url, Mono<String> data) {
        Mono<Tuple2<String, Optional<Object>>> dataAndContext =
                data.zipWith(Mono.subscriberContext().map(c -> c.getOrEmpty(HTTP_CORRELATION_ID)));

        return dataAndContext
                .<String>handle((dac, sink) -> {
                    if (dac.getT2().isPresent()) {
                        sink.next("PUT <"
                                          + dac.getT1()
                                          + "> sent to "
                                          + url
                                          + " with header X-Correlation-ID = "
                                          + dac.getT2().get());
                    } else {
                        sink.next("PUT <" + dac.getT1() + "> sent to " + url);
                    }
                    sink.complete();
                })
                .map(msg -> Tuples.of(200, msg));
    }

    public void contextForLibraryReactivePut() {
        Mono<String> put = doPut("www.example.com", Mono.just("Walter"))
                .subscriberContext(Context.of(HTTP_CORRELATION_ID, "2-j3r9afaf92j-afkaf"))
                .filter(t -> t.getT1() < 300)
                .map(Tuple2::getT2);

    }
}
