import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class LowLevelStreamingDemo {

    public static void main(String[] args) throws InterruptedException {
        OpenAiStreamingChatModel model = OpenAiStreamingChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName("gpt-4o-mini")          // use a model your account supports
                .timeout(Duration.ofSeconds(60))   // optional but helpful
                .build();

        CountDownLatch done = new CountDownLatch(1);

        model.chat("Can I use Java for GenAI applications?",
                new StreamingChatResponseHandler() {
                    @Override
                    public void onPartialResponse(String partial) {
                        System.out.print(partial);
                        System.out.flush();
                    }

                    @Override
                    public void onCompleteResponse(ChatResponse complete) {
                        done.countDown();
                    }

                    @Override
                    public void onError(Throwable error) {
                        error.printStackTrace();
                        done.countDown();
                    }
                }
        );

        // Block until stream finishes (or time out to avoid hanging forever)
        if (!done.await(90, TimeUnit.SECONDS)) {
            System.err.println("\n[warn] stream timed out");
        }
    }
}