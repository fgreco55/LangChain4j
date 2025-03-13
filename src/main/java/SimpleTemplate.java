
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class SimpleTemplate {
    public static void main(String[] args) {
        ChatLanguageModel cmodel = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(OpenAiChatModelName.GPT_4_O)
                .temperature(.3)                    // keep randomness low
                .timeout(Duration.ofSeconds(120))
                .maxTokens(1024)
                .build();

        String myTemplate = "Please explain {{topic}} to a {{student_level}} student using a clear, succinct paragraph";
        PromptTemplate promptTemplate = PromptTemplate.from(myTemplate);
        Map<String, Object> variables = new HashMap<>();
        variables.put("topic", "machine learning");
        variables.put("student_level", "grammar school");

        Prompt prompt = promptTemplate.apply(variables);
        String response = cmodel.chat(prompt.text());

        System.out.println(response);
    }
}
