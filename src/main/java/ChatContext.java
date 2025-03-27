import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;

import java.util.List;
import java.util.Scanner;

public class ChatContext {
    public static void main(String[] args) {
        Scanner userinput; 
        String cmdline;
        ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(1000);
        ChatMessage cm;

        SystemMessage sysmsg = new SystemMessage("""
                    You are a polite Java consultant with deep expertise in teaching AI and Machine Learning.
                """);
        chatMemory.add(sysmsg);

        ChatLanguageModel cmodel = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(OpenAiChatModelName.GPT_4_O)
                .build();

        while (true) {
            System.out.print("prompt> ");

            userinput = new Scanner(System.in);
            cmdline = userinput.nextLine();

            if (cmdline.isBlank())       // If nothing, do nothing
                continue;

            UserMessage usrmsg = UserMessage.from(cmdline);   // create the prompt
            chatMemory.add(usrmsg);

            var answer = cmodel.chat(chatMemory.messages());  // send the context as messages and save the response
            var response = answer.aiMessage().text();

            System.out.println(response);

            chatMemory.add(UserMessage.from(response));     // Add the response from the assistant
            chatMemory.add(UserMessage.from(cmdline));      // Add the prompt from the user

            //dumpMemory(chatMemory);
        }
    }
    static void dumpMemory(ChatMemory chatMemory) {
        List<ChatMessage> memory = chatMemory.messages();
        System.out.println("============================");
        for (ChatMessage m : memory) {
            if (m instanceof SystemMessage) {
                System.out.println("SystemMessage: " + m.toString());
            } else if (m instanceof UserMessage) {
                System.out.println("UserMessage: " + m.toString());
            }
        }
        System.out.println("============================");
    }
}
