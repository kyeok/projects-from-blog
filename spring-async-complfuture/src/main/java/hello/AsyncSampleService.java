package hello;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Created by Harley on 2016. 9. 11..
 */
@Service
public class AsyncSampleService {
    private Logger log = Logger.getLogger(this.getClass());

    @Autowired
    private GitHubLookupService gitHubLookupService;

    public void serve1() throws InterruptedException, ExecutionException, TimeoutException {
        final Future<User> responseFuture = gitHubLookupService.findUser("91ft");
        final User response = responseFuture.get(1, SECONDS);
        send(response);
    }

    public void serve2() throws InterruptedException, ExecutionException, TimeoutException {
        final CompletableFuture<User> responseFuture = gitHubLookupService.findUser("91ft");
        final User response = responseFuture.get(1, SECONDS);
        send(response);
    }

    public void serve3() throws InterruptedException {
        final CompletableFuture<User> responseFuture = gitHubLookupService.findUser("91ft");
        responseFuture.thenAccept(this::send);
    }

    public void serve4() throws InterruptedException {
        final CompletableFuture<User> responseFuture = gitHubLookupService.findUser("91ft");
        responseFuture
                .exceptionally(throwable -> {
                    log.error("Unrecoverable error", throwable);
                    return null;
                });
        responseFuture.thenAccept(this::send);
    }

    public void serve5() throws InterruptedException {
        final CompletableFuture<User> responseFuture = gitHubLookupService.findUser("91ft");
        responseFuture
                .exceptionally(throwable -> {
                    log.error("Unrecoverable error", throwable);
                    return null;
                })
                .thenAccept(this::send);  //probably not what you think
    }

    private static final ScheduledExecutorService scheduler =
            //1 thread ought to be enough for anybody
            Executors.newScheduledThreadPool(
                    1,
                    new ThreadFactoryBuilder()
                            .setDaemon(true)
                            .setNameFormat("failAfter-%d")
                            .build());


    public static <T> CompletableFuture<T> failAfter(Duration duration) {
        final CompletableFuture<T> promise = new CompletableFuture<T>();
        scheduler.schedule(() -> {
            final TimeoutException ex = new TimeoutException("Timeout after " + duration);
            return promise.completeExceptionally(ex);
        }, duration.toMillis(), MILLISECONDS);
        return promise;
    }

    public void serve6_withTimeout() throws InterruptedException {
        final CompletableFuture<User> responseFuture = gitHubLookupService.findUser("91ft");
        final CompletableFuture<User> oneSecondTimeout = failAfter(Duration.ofSeconds(1));
//        final CompletableFuture<User> oneSecondTimeout = failAfter(Duration.ofMillis(0));
        responseFuture.acceptEither(oneSecondTimeout, this::send).exceptionally(throwable -> {
            log.error("Problem", throwable);
            return null;
        });
    }

    public static <T> CompletableFuture<T> within(CompletableFuture<T> future, Duration duration) {
        final CompletableFuture<T> timeout = failAfter(duration);
        return future.applyToEither(timeout, Function.identity());
    }

    public void serve7_within() throws InterruptedException {
        final CompletableFuture<User> responseFuture = within(gitHubLookupService.findUser("91ft"), Duration.ofMillis(1));
        responseFuture
                .thenAccept(this::send)
                .exceptionally(throwable -> {
                    log.error("Unrecoverable error", throwable);
                    return null;
                });
    }

    private void send(User user) {
        log.info("Hello~ " + user.getName());
    }

}
