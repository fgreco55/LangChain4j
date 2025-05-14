import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.data.message.ChatMessage;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;

import java.util.ArrayList;
import java.util.List;

public class HelloWorld {
    public static void main(String[] argv) {
        String apiKey = System.getenv("OPENAI_API_KEY");

        ChatModel cmodel = OpenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName(OpenAiChatModelName.GPT_4_O)
                .build();

        List<ChatMessage> messages = new ArrayList<>();
        //UserMessage usrmsg = UserMessage.from("Why should I learn Java.");
        UserMessage usrmsg = UserMessage.from("Why should I learn Java.");
        //messages.add(usrmsg);

        //var answer = cmodel.chat(messages);
        var answer = cmodel.chat(usrmsg);
        
        System.out.println(answer.aiMessage().text());
    }
}
