import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;

import java.io.Console;
import java.time.Duration;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_O_MINI;

public class ServicesStreamLatchTypewriter {
    private static String question;

    interface Assistant {
        @SystemMessage("Respond as a professional enterprise consultant without using markdown or numbered bullets.")
        TokenStream chat(@UserMessage String message);
    }

    public static void main(String[] args) throws InterruptedException {

        StreamingChatModel model = OpenAiStreamingChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(GPT_4_O_MINI)
                .timeout(Duration.ofSeconds(120))
                .maxTokens(1024*10)
                .build();

        Assistant consultant = AiServices.builder(Assistant.class)
                .streamingChatModel(model)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build();

        String pstring = "\nCmd> ";

        Set<String> set = Set.of("exit", "quit", "bye");
        Console console = System.console();

        while (true) {
            question = console.readLine(pstring);
            if (set.contains(question.toLowerCase()))
                break;

            var latch = new CountDownLatch(1);

            consultant.chat(question)
                    .onPartialResponse(tok -> typewriterPrint(tok, 10))                 // tokens/chunks as they arrive
                    .onCompleteResponse(r -> {
                        latch.countDown();
                    })
                    .onError(e -> {
                        e.printStackTrace();
                        latch.countDown();
                    })
                    .start();

            latch.await();
        }
    }
    static void typewriterPrint(String chunk, int delayMs) {
        for (int i = 0; i < chunk.length(); i++) {
            System.out.print(chunk.charAt(i));
            try {
                Thread.sleep(delayMs);      // simplistic... not good for production quality
            } catch (InterruptedException ignored) {
            }
        }
        System.out.flush();
    }
}

