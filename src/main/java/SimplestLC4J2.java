import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;

public class SimplestLC4J2 {
    public static void main(String[] args) {
        System.out.println(
                OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(OpenAiChatModelName.GPT_4_O)
                .build()
                .chat(UserMessage.from("Should I learn Java?"))
                .aiMessage()
                .text()
        );
    }
}
