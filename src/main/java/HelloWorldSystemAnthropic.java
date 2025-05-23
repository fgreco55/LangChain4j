import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static dev.langchain4j.model.anthropic.AnthropicChatModelName.CLAUDE_3_5_SONNET_20241022;

public class HelloWorldSystemAnthropic {
    public static void main(String[] argv) {
        ChatModel cmodel = AnthropicChatModel.builder()
                .apiKey(System.getenv("ANTHROPIC_API_KEY"))
                .modelName(CLAUDE_3_5_SONNET_20241022)
                .timeout(Duration.ofSeconds(120))
                .maxTokens(256)
                .build();

        List<ChatMessage> messages = new ArrayList<>();

        SystemMessage sysmsg = new SystemMessage("""
                    You are a polite Java expert explaining concepts to a C++ software developer.
                """);
        messages.add(sysmsg);

        UserMessage usrmsg = UserMessage.from("What are Java lambdas?");
        messages.add(usrmsg);

        var answer = cmodel.chat(messages);

        System.out.println(answer.aiMessage().text());
    }
}
