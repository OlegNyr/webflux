package ru.nyrk.web.flux.webflux;

import com.netflix.hystrix.HystrixCommandProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.hystrix.HystrixCommands;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * todo:java doc
 */
@RestController
public class FluxController {
    private static final Logger logger = LoggerFactory.getLogger(FluxController.class);

    @GetMapping("/delay/{count}")
    Mono<String> delay(@PathVariable("count") int count) {
        ExecutorService executorService = Executors.newFixedThreadPool(count);
        for (int i = 0; i < count; i++) {
            final int cnt = i;
            executorService.submit(() -> executeService(cnt));
        }
        return Mono.just("ok");
    }

    private void executeService(int cnt) {
        logger.info("Start execute N {}", cnt);
        Mono<ClientResponse> exchange = WebClient
                .create()
                .get()
                .uri("http://localhost:9080/delay/1000/200")
                .exchange();

        Mono<String> stringMono = HystrixCommands
                .from(exchange)
                .commandProperties(HystrixCommandProperties.Setter()
                                           .withExecutionTimeoutInMilliseconds(20000)
                                           .withMetricsRollingPercentileEnabled(true)
                                           //Это свойство указывает, следует ли отслеживать задержки выполнения
                                           // и рассчитывать их как процентили
                                           .withMetricsRollingPercentileWindowInMilliseconds(60000)
                                           //Это свойство задает длительность окна качения, в котором хранятся
                                           // время выполнения, чтобы разрешить вычисления процентилей в миллисекундах.
                                           .withMetricsRollingPercentileWindowBuckets(6)
                                           //Это свойство задает количество ведер, в rollingPercentileкоторое
                                           // будет разбито окно.
                                           .withMetricsHealthSnapshotIntervalInMilliseconds(500)
                                           //Это свойство задает время ожидания в миллисекундах между
                                           // разрешением моментальных снимков работоспособности, которые вычисляют успехи
                                           // и проценты ошибок и влияют на состояние автоматического выключателя.
                                           //В схемах с большим объемом непрерывный расчет процентных погрешностей
                                           // может стать интенсивным, поэтому это свойство позволяет вам контролировать,
                                           // как часто он рассчитывается.
                                           .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE)
                                           .withExecutionTimeoutInMilliseconds(1000)
                                           //Это свойство задает время в миллисекундах, после которого
                                           // вызывающий абонент будет наблюдать тайм-аут и уйти от выполнения команды.
                                           .withExecutionIsolationSemaphoreMaxConcurrentRequests(10)
                                           //Это свойство устанавливает максимальное количество запросов
                                           .withCircuitBreakerEnabled(true)
                                           //автоматический выключатель использоваться для отслеживания
                                           // состояния здоровья и запросов на короткое замыкание, если он отключится
                                           .withCircuitBreakerRequestVolumeThreshold(20)
                                           //Это свойство задает минимальное количество запросов в окне качения, которое отключает схему
                                           .withCircuitBreakerSleepWindowInMilliseconds(5000)
                                           //Это свойство устанавливает время после отключения цепи,
                                           // чтобы отклонить запросы, прежде чем снова попытаться определить,
                                           // должна ли цепь снова быть закрыта.
                                           .withCircuitBreakerErrorThresholdPercentage(50)
                                           //Это свойство задает процент ошибки, при котором или выше
                                           // которого цепь должна отключиться и запускать запросы на короткое замыкание на резервную логику.
                                           .withCircuitBreakerForceOpen(false)
                                           .withCircuitBreakerForceClosed(false)
                )
                .commandName("delayCommand")
                .toMono()
                .flatMap(l -> l.bodyToMono(String.class));
        try {
            logger.info("result {}", stringMono.block());
        } catch (Exception e) {
            logger.error("Error {} {}", e.getClass(), e.getMessage());
        }
    }

}
