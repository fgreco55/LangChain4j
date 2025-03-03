import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.anthropic.AnthropicChatModel;
import static dev.langchain4j.model.anthropic.AnthropicChatModelName.CLAUDE_3_5_SONNET_20241022;

import java.util.ArrayList;
import java.util.List;

public class HelloWorldAnthropic {
    public static void main(String[] argv) {
        String apiKey = System.getenv("ANTHROPIC_API_KEY");

        ChatLanguageModel model = AnthropicChatModel.builder()
                .apiKey(apiKey)
                .modelName(CLAUDE_3_5_SONNET_20241022)
                .build();

        List<ChatMessage> messages = new ArrayList<>();
        UserMessage usrmsg = UserMessage.from("Why should I learn Java.");
        messages.add(usrmsg);

        var answer = model.chat(messages);

        System.out.println(answer.aiMessage().text());
    }
}
