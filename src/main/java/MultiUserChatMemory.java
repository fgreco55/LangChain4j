import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MultiUserChatMemory {
    public interface Support {
        @SystemMessage("Respond as a helpful and polite customer support representative.")
        String send(@MemoryId String memId, @UserMessage String msg);
    }

    public static void main(String[] args) {
        Scanner userinput;
        String cmdline;

        ChatModel model = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(OpenAiChatModelName.GPT_4_O)
                .build();

        Support support = AiServices.builder(Support.class)
                .chatModel(model)
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(10))
                .build();

        while (true) {
            System.out.print("Question> ");
            cmdline = new Scanner(System.in).nextLine();

            if (cmdline.isBlank())       // If nothing, do nothing
                continue;

            List<String> commands = simpleParser(cmdline);  // cheapo parser - good enough for our example
            if (commands.size() != 2)
                continue;

            var answer = support.send(commands.get(0), commands.get(1));  // send the context as messages and save the response

            System.out.println(answer);
        }
    }

    /**
     * simpleParser() - trivial parser to split context from the request
     * @param input - string to be parsed.  First part is up to the first ":", second part is the remainder
     * @return List of Strings in the input.  Valid input only returns 2 arguments.
     */
    static List<String> simpleParser(String input) {
        List<String> result = new ArrayList<>();

        int colonIndex = input.indexOf(":");
        if (colonIndex != -1) {
            result.add(input.substring(0, colonIndex));
            result.add(input.substring(colonIndex + 1));
        }

        return result;
    }
}
