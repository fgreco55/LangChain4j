import java.io.Console;
import java.time.Duration;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_O_MINI;

class ServicesStream {
    private static String question;

    interface Assistant {
        @SystemMessage("Respond as a professional enterprise consultant without using markdown or numbered bullets.")
        TokenStream chat(String message);
    }

    public static void main(String[] args) {

        StreamingChatModel model = OpenAiStreamingChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(GPT_4_O_MINI)
                .timeout(Duration.ofSeconds(120))
                .logRequests(true)
                //.logResponses(true)
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

            CompletableFuture<ChatResponse> future = new CompletableFuture<>();
            TokenStream stream = consultant.chat(question);

            stream  .onPartialResponse(System.out::print)
                    .onCompleteResponse(future::complete)
                    .onError(future::completeExceptionally)
                    .start();

            future.join();
        }

        System.exit(0);
    }
}
