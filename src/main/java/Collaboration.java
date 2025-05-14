import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.model.mistralai.MistralAiChatModel;
import dev.langchain4j.model.mistralai.MistralAiChatModelName;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;

import java.util.ArrayList;
import java.util.List;

public class Collaboration {
    public static void main(String[] argv) throws Exception {
        UserMessage userMessage;
        List<ChatMessage> messages = new ArrayList<>();
        String completion;

        /*****  Send to OpenAI *****/
        userMessage = UserMessage.from("Give me a one-paragraph description of what machine learning is.");
        messages.add(userMessage);
        completion = sendToLLM("openai", messages);
        System.out.println("[ORIGINAL]=======================\n" + completion);

        messages.add(UserMessage.from(completion));               // Add result to context

        /***** Send to Gemini *****/
        userMessage = UserMessage.from("An LLM described machine learning as "
                + completion + """
                    Rate this response from that other LLM in four different categories: 
                    correctness, conciseness, comprehensiveness and accuracy. 
                    Give it a grade from 0 to 10 for each category 
                    where 0 is not correct, not concise, incomprehensive and not accurate, 
                    and where 10 is 100 percent correct, extremely concise, perfectly understood, and 100 percent accurate.
                """);

        messages.add(userMessage);               // Add prompt to overall context
        completion = sendToLLM("gemini", messages);
        System.out.println("[EVALUATION]=======================\n" + completion);

        messages.add(UserMessage.from(completion));

        /***** Send to Mistral *****/
        userMessage = UserMessage.from("""
            Modify the original LLM's response to
            defining Machine Learning in one paragraph so that it rates
            10 out of 10 for every category.""");
        messages.add(userMessage);
        completion = sendToLLM("mistral", messages);
        System.out.println("[NEW]=======================\n" + completion);
    }

    /**
     * sendToLLM() - convenience method for sending prompts to LLMs
     * @param vendor - "openai", "google", "mistral"
     * @param chatMessages - List of prompt messages
     * @return completion from the LLM
     * @throws Exception
     */
    static String sendToLLM(String vendor, List<ChatMessage> chatMessages) throws Exception {
        ChatModel clm = switch (vendor.toLowerCase()) {
            case "openai" ->
                OpenAiChatModel.builder()
                        .apiKey(System.getenv("OPENAI_API_KEY"))
                        .modelName(OpenAiChatModelName.GPT_3_5_TURBO)
                        .temperature(1.7)   // make it overly creative so there's opportunity to fix
                        .build();
            case "gemini" ->
                GoogleAiGeminiChatModel.builder()
                        .apiKey(System.getenv("GEMINI_API_KEY"))
                        .modelName("gemini-1.5-flash")
                        .build();
            case "mistral" ->
                MistralAiChatModel.builder()
                        .apiKey(System.getenv("MISTRAL_API_KEY"))
                        //.modelName(String.valueOf(MistralAiChatModelName.OPEN_MISTRAL_7B))
                        .modelName(MistralAiChatModelName.OPEN_MISTRAL_7B)
                        .build();
            default -> throw new Exception("Unknown GenAI model: " + vendor);
        };
        ChatResponse chatResponse = clm.chat(chatMessages);
        String completion = chatResponse.aiMessage().text();
        
        return completion;
    }
}
