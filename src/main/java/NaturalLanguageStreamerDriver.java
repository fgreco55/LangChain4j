import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;

import java.io.Console;
import java.time.Duration;
import java.util.Set;

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_O_MINI;

public class NaturalLanguageStreamerDriver {
    public interface Consultant {
        @SystemMessage("You are a polite technology consultant")
        TokenStream consult(String text);
    }

    public static void main(String[] args) {
        String question;
        String pstring = "\nCmd> ";

        StreamingChatModel model = OpenAiStreamingChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(GPT_4_O_MINI)
                .timeout(Duration.ofSeconds(120))
                .maxTokens(1024 * 10)
                .build();
        Consultant consultant = AiServices.builder(Consultant.class)
                .streamingChatModel(model)
                .build();

        Set<String> set = Set.of("exit", "quit", "bye");
        Console console = System.console();
        NaturalLanguageStreamer streamer = new NaturalLanguageStreamer(
                System.out::print, 5, 15
        );

        while (true) {
            question = console.readLine(pstring);
            if (set.contains(question.toLowerCase()))
                break;

            consultant.consult(question)
                    .onPartialResponse(streamer.onPartialResponse())
                    .onCompleteResponse(streamer.onCompleteResponse())
                    .onError(streamer.onError())
                    .start();

            streamer.awaitCompletion();
        }
    }
}
