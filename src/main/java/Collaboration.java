import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
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

        messages.add(UserMessage.from(completion));               // Add result to context

        /***** Send to Gemini *****/
        userMessage = UserMessage.from("An LLM described machine learning as "
                + completion + " "
                + "Rate this response from that other LLM in four different categories: "
                + "correctness, conciseness, comprehensiveness and accuracy. "
                + "Give it a grade from 0 to 10 for each category "
                + "where 0 is not correct, not concise, incomprehensive and not accurate, "
                + "and where 10 is 100 percent correct, extremely concise, perfectly understood, and 100 percent accurate.");
        messages.add(userMessage);               // Add prompt to overall context
        completion = sendToLLM("gemini", messages);

        messages.add(UserMessage.from(completion));

        /***** Send to Mistral *****/
        userMessage = UserMessage.from("Rewrite the original LLM's response so that it rates 10 out of 10 for every category.");
        messages.add(userMessage);
        completion = sendToLLM("mistral", messages);
    }

    /**
     * sendToLLM() - convenience method for sending prompts to LLMs
     * @param vendor - "openai", "google", "mistral"
     * @param chatMessages - List of prompt messages
     * @return completion from the LLM
     * @throws Exception
     */
    static String sendToLLM(String vendor, List<ChatMessage> chatMessages) throws Exception {
        String lowerVendor = vendor.toLowerCase();
        ChatLanguageModel clm = switch (lowerVendor) {
            case "openai" ->
                OpenAiChatModel.builder()
                        .apiKey(System.getenv("OPENAI_API_KEY"))
                        .modelName(OpenAiChatModelName.GPT_3_5_TURBO)
                        .build();
            case "gemini" ->
                GoogleAiGeminiChatModel.builder()
                        .apiKey(System.getenv("GEMINI_API_KEY"))
                        .modelName("gemini-1.5-flash")
                        .build();
            case "mistral" ->
                MistralAiChatModel.builder()
                        .apiKey(System.getenv("MISTRAL_API_KEY"))
                        .modelName(String.valueOf(MistralAiChatModelName.OPEN_MISTRAL_7B))
                        .build();
            default -> throw new Exception("Unknown GenAI model: " + vendor);
        };
        ChatResponse chatResponse = clm.chat(chatMessages);
        String completion = chatResponse.aiMessage().text();

        System.err.println(completion);
        System.out.println("=======================================");
        return completion;
    }
}
