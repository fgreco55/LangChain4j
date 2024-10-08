import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.StreamingResponseHandler;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.model.output.Response;

import java.io.IOException;
import java.util.Scanner;

import static java.lang.Thread.sleep;

public class StreamingChat {
    private static boolean streamOn = false;

    public static void main(String[] args) throws IOException, InterruptedException {
        Scanner userinput;                                  // user inputted line as a Scanner
        String cmdline;

        StreamingChat schat = new StreamingChat();
        StreamingChatLanguageModel model = OpenAiStreamingChatModel.withApiKey(System.getenv("OPENAI_API_KEY"));
        streamOn = false;

        while (true) {

            userinput = new Scanner(System.in);

            while (streamOn) {          // wait for stream completion to terminate
                sleep(500);
            }

            System.out.print("prompt> ");
            cmdline = userinput.nextLine();

            if (cmdline.isEmpty())
                continue;

            streamOn = true;
            model.generate(cmdline, new StreamingResponseHandler<AiMessage>() {

                @Override
                public void onNext(String token) {
                    System.out.print(token);    // display each chunk as it comes in
                }

                @Override
                public void onComplete(Response<AiMessage> response) {
                    System.out.println();       // put "prompt>" on next line
                    streamOn = false;           // if streamOn is false, the LLM is done sending it's completion
                    //System.out.println(response.content().text());    // the entire result
                }

                @Override
                public void onError(Throwable error) {
                    error.printStackTrace();
                }
            });
        }
    }
}
