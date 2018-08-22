package ru.nyrk.web.flux;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Hooks;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import ru.nyrk.web.flux.webflux.MDCSetupExecutorService;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.function.Supplier;

@SpringBootApplication
public class FluxApplication {

    public static void main(String[] args) {
        Hooks.onOperatorDebug();
        Schedulers.setFactory(new Schedulers.Factory() {
            @Override
            public ScheduledExecutorService decorateExecutorService(String schedulerType,
                                                                    Supplier<? extends ScheduledExecutorService> actual) {
                return new MDCSetupExecutorService(actual.get());
            }

        });
        SpringApplication.run(FluxApplication.class, args);
    }
}
