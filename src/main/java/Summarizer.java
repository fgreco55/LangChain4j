import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static dev.langchain4j.model.anthropic.AnthropicChatModelName.CLAUDE_3_5_SONNET_20241022;

public class Summarizer {
    public static void main(String[] args) {
        String cmdline;
        List<ChatMessage> messages;
        SystemMessage sysmsg = SystemMessage.from("""
                You are an expert administrator with expertise in summarizing complex texts
                """);

        ChatLanguageModel model = AnthropicChatModel.builder()
                .apiKey(System.getenv("ANTHROPIC_API_KEY"))
                .modelName(CLAUDE_3_5_SONNET_20241022)
                .build();

        while (true) {
            messages = new ArrayList<>();
            messages.add(sysmsg);

            String fname = getUserInput("File> ");         // minimal error checking
            String level = getUserInput("Level> ");
            String language = getUserInput("Language> ");

            UserMessage usrmsg = UserMessage.from(genPrompt(fname, level, language));
            messages.add(usrmsg);

            ChatResponse answer = model.chat(messages);
            System.out.println(answer.aiMessage().text());
        }
    }

    static public String genPrompt(String fileName, String summary_level, String language) {
        String myTemplate = """
                Please create a summary from the following text at a {{level}} level using a clear, succinct paragraph
                that captures the essence of the text, highlighting key themes and insights.  
                Respond in {{language}}.  {{file}}
                """;
        PromptTemplate promptTemplate = PromptTemplate.from(myTemplate);

        Map<String, Object> variables = new HashMap<>();
        variables.put("level", summary_level);
        String pathname = System.getProperty("user.dir") + "/src/main/resources/" + fileName;
        variables.put("file", TextfiletoString(pathname));
        variables.put("language", language);

        Prompt prompt = promptTemplate.apply(variables);
        return prompt.text();
    }

    static public String TextfiletoString(String filename) {
        String bigString = (String) null;

        try {
            Path path = Path.of(filename);
            byte[] bytes = Files.readAllBytes(path);
            bigString = new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("***ERROR: Cannot read file. " + e.getMessage());
        }
        return bigString;
    }

    static public String getUserInput(String pstring) {
        String cmdline;

        System.out.print(pstring);
        cmdline = new Scanner(System.in).nextLine();
        return cmdline.isBlank() ? "" : cmdline;
    }
}
