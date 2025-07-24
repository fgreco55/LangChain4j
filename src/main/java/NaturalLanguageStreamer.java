import dev.langchain4j.model.chat.response.ChatResponse;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class NaturalLanguageStreamer {
    private final BlockingQueue<String> charQueue = new LinkedBlockingQueue<>();  // ✅ fixed
    private final ExecutorService worker = Executors.newSingleThreadExecutor();
    private final CompletableFuture<Void> done = new CompletableFuture<>();
    private final AtomicBoolean streaming = new AtomicBoolean(true);

    private final Consumer<String> output;
    private final long minDelayMillis;
    private final long maxDelayMillis;

    public NaturalLanguageStreamer(Consumer<String> output, int minDelayMillis, int maxDelayMillis) {
        this.output = output;
        this.minDelayMillis = minDelayMillis;
        this.maxDelayMillis = maxDelayMillis;
        output.accept("...\n");
        startWorker();
    }

    public Consumer<String> onPartialResponse() {
        return token -> {
            for (char c : token.toCharArray()) {
                charQueue.offer(String.valueOf(c));
            }
        };
    }

    public Consumer<ChatResponse> onCompleteResponse() {
        return response -> {
            streaming.set(false);
            done.complete(null);
        };
    }

    public Consumer<Throwable> onError() {
        return error -> {
            streaming.set(false);
            done.completeExceptionally(error);
        };
    }

    public void awaitCompletion() {
        done.join();
        worker.shutdownNow();
    }

    private void startWorker() {
        worker.submit(() -> {
            try {
                while (streaming.get() || !charQueue.isEmpty()) {
                    String nextChar = charQueue.poll(100, TimeUnit.MILLISECONDS);
                    if (nextChar != null) {
                        output.accept(nextChar);
                        Thread.sleep(randomDelayMillis());
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    private long randomDelayMillis() {
        return ThreadLocalRandom.current().nextLong(minDelayMillis, maxDelayMillis + 1);
    }
}
