import dev.langchain4j.model.chat.response.ChatResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;
import java.util.function.Consumer;

public class SmoothStreamingHandler {

    private final BlockingQueue<String> queue = new LinkedBlockingQueue<>();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final CompletableFuture<Void> done = new CompletableFuture<>();
    private final Consumer<String> outputSink;
    private final int tokensPerSecond;

    private int LENGTH = 0;

    public SmoothStreamingHandler(int tokensPerSecond, Consumer<String> outputSink) {
        this.tokensPerSecond = tokensPerSecond;
        this.outputSink = outputSink;
        final Logger logger = LoggerFactory.getLogger(SmoothStreamingHandler.class);

        long delay = 1000L / tokensPerSecond;
        scheduler.scheduleAtFixedRate(() -> {
            String token = queue.poll();
            if (token != null) {
                outputSink.accept(token);
                LENGTH += token.length();
                logger.debug("[" + LENGTH + ":" + token + "] -----------------------");
            }
        }, 0, delay, TimeUnit.MILLISECONDS);
    }

    public Consumer<String> queueOffer() {
        return queue::offer;
    }

    public Consumer<ChatResponse> onComplete() {
        return resp -> done.complete(null);
    }

    public Consumer<Throwable> onError() {
        return done::completeExceptionally;
    }

    public void awaitDone() {
        done.join();
        scheduler.shutdownNow();
    }
}