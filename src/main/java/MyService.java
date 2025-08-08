import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public class MyService {
    interface Consultant {
        @SystemMessage("You are a polite technology consultant")
        @UserMessage("What are the benefits of Java in 5 lines")
        String consult();

        @SystemMessage("You are a hilariously funny junior developer")
        String funny(String text);
    }

    public static void main(String[] args) {
        ChatModel cmodel = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(OpenAiChatModelName.GPT_4_O)
                .build();

        Consultant consultant = AiServices.create(Consultant.class, cmodel);

        System.out.println("PROFESSIONAL====================================");
        System.out.println(consultant.consult());
        System.out.println("COMEDIAN========================================");
        System.out.println(consultant.funny("What are the benefits of Java in 5 lines"));
    }
}
