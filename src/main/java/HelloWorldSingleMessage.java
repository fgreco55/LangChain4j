import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;

import java.util.ArrayList;
import java.util.List;

public class HelloWorldSingleMessage {public static void main(String[] args) {
        String apiKey = System.getenv("OPENAI_API_KEY");

        ChatLanguageModel cmodel = OpenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName(OpenAiChatModelName.GPT_4_O)
                .build();

        UserMessage usrmsg = UserMessage.from("Why should I learn Java.");
        var answer = cmodel.chat(usrmsg);

        System.out.println(answer.aiMessage().text());
    }

}
