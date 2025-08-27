import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.model.openai.OpenAiChatModel;

public class AiServicesPromptExample {

    interface Translator {
        @UserMessage("Translate the following text into {{language}}:\n{{text}}")
        String translate(String language, String text);
    }

    public static void main(String[] args) {
        // 1. Create model
        OpenAiChatModel model = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName("gpt-4o-mini")
                .build();

        // 2. Create service proxy
        Translator translator = AiServices.builder(Translator.class)
                .chatModel(model)
                .build();

        // 3. Call method with variables — automatically substituted into the template
        String response = translator.translate("French", "Hello, how are you?");

        // 4. Output
        System.out.println("Model response:\n" + response);
    }
}

