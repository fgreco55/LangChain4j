import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;

import java.io.Console;
import java.time.Duration;
import java.util.Set;

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_O;
import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_O_MINI;

/*
 better than raw SSE, but still janky - see NaturalLanguageStreamer
 */

public class SmoothStreamingDriver {
    public interface Consultant {
        @SystemMessage("You are a polite technology consultant")
        TokenStream consult(String text);
    }

    public static void main(String[] args) {
        String question;
        String pstring = "\nCmd> ";

        StreamingChatModel model = OpenAiStreamingChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(GPT_4_O)
                .timeout(Duration.ofSeconds(120))
                .maxTokens(1024*10)     // IGNORED... SEEMS TO BE A LC4J BUG... fdg
                .build();
        Consultant consultant = AiServices.builder(Consultant.class)
                .streamingChatModel(model)
                .build();

        Set<String> set = Set.of("exit", "quit", "bye");
        Console console = System.console();
       // SmoothStreamingHandler streamer = new SmoothStreamingHandler(10, System.out::print);

        while (true) {
            question = console.readLine(pstring);
            if (set.contains(question.toLowerCase()))
                break;

            SmoothStreamingHandler streamer = new SmoothStreamingHandler(15, System.out::print);

            TokenStream stream = consultant.consult(question);
            stream.onPartialResponse(streamer.queueOffer())
                    .onCompleteResponse(streamer.onComplete())
                    .onError(streamer.onError())
                    .start();
            streamer.awaitDone();
        }
    }
}