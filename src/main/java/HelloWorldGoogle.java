import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.model.chat.ChatModel;

import java.util.ArrayList;
import java.util.List;

public class HelloWorldGoogle {
    public static void main(String[] argv) {
            String apiKey = System.getenv("GOOGLE_API_KEY");

            ChatModel model = GoogleAiGeminiChatModel.builder()
                    .apiKey(apiKey)
                    .modelName("gemini-1.5-flash")
                    .build();

            List<ChatMessage> messages = new ArrayList<>();
            UserMessage usrmsg = UserMessage.from("Why should I learn Java.");
            messages.add(usrmsg);

            var answer = model.chat(messages);

            System.out.println(answer.aiMessage().text());
        }
}
