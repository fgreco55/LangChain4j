import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

import static dev.langchain4j.model.openai.OpenAiEmbeddingModelName.TEXT_EMBEDDING_3_SMALL;

public class EmbeddingInMemory {

    public static void main(String[] args) {
        InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

        EmbeddingModel emodel = OpenAiEmbeddingModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(TEXT_EMBEDDING_3_SMALL)
                .build();

        loadEmbeddingsFromFile(embeddingStore, emodel, "src/main/resources/local_data_small.txt");

        while (true) {
            System.out.print("query> ");
            String cmdline = new Scanner(System.in).nextLine();

            Embedding queryEmbedding = emodel.embed(cmdline).content();

            EmbeddingSearchRequest embeddingSearchRequest = EmbeddingSearchRequest.builder()
                    .queryEmbedding(queryEmbedding)
                    .maxResults(10)     // at most, find this number of matches
                    .minScore(0.7)      // 0-1, so ignore anything below the midpoint
                    .build();

            List<EmbeddingMatch<TextSegment>> matches = embeddingStore.search(embeddingSearchRequest).matches();

            if (matches.isEmpty()) {
                System.out.println("No matches found");
            }

            matches.forEach(em -> System.out.println(em.score() + ":" + em.embedded()));
        }
    }

    /**
     * loadEmbeddingsFromFile() - Given an EmbeddingStore, an EmbeddingModel, and a file of strings, store the embedding vectors and the strings in the EmbeddingStore
     *
     * @param estore   - where you want to store the strings and vectors
     * @param embModel - which particular embedding model you want
     * @param myfile   - file of strings.  Each is delimited by a CR
     */
    public static void loadEmbeddingsFromFile(EmbeddingStore<TextSegment> estore, EmbeddingModel embModel, String myfile) {

        try (Stream<String> lines = Files.lines(Path.of(myfile))) {
            lines.forEach(line -> {
                TextSegment ts = TextSegment.from(line);
                Embedding emb = embModel.embed(ts).content();
                estore.add(emb, ts);
            });
            System.out.println("Embedding and Loading completed...");
        } catch (IOException iox) {
            iox.printStackTrace();
        }
    }
}
