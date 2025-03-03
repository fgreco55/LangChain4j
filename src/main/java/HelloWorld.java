import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.data.message.ChatMessage;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class HelloWorld {
    public static void main(String[] argv) {
        String apiKey = System.getenv("OPENAI_API_KEY");

        ChatLanguageModel cmodel = OpenAiChatModel.builder()
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
