import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.output.Response;

import java.io.IOException;
import java.util.List;

import static dev.langchain4j.model.openai.OpenAiEmbeddingModelName.TEXT_EMBEDDING_3_SMALL;

public class CompareEmbeddings {
    public static void main(String[] args) throws IOException {

            EmbeddingModel model = OpenAiEmbeddingModel.builder()
                    .apiKey(System.getenv("OPENAI_API_KEY"))
                    .modelName(TEXT_EMBEDDING_3_SMALL)
                    .build();

            List<Float> one = getEmbeddingVec(model, "I like the Java programming language.");
            List<Float> two = getEmbeddingVec(model, "Tonight is pizza night at the baseball stadium.");

            double similarity = cosineSimilarity(FloatList2doubleArray(one), FloatList2doubleArray(two));
            System.out.println("Cosine Similarity: " + similarity);
            //System.out.println("Cosine Distance: " + (1 - similarity));
        }

        public static List<Float> getEmbeddingVec(EmbeddingModel model, String input) {
            Response<Embedding> response = model.embed(input);
            return response.content().vectorAsList();
        }

        public static double[] FloatList2doubleArray(List<Float> floatList) {
            double[] result = new double[floatList.size()];
            for (int i = 0; i < floatList.size(); i++) {
                result[i] = floatList.get(i);
            }
            return result;
        }

        public static double dotProduct(double[] vec1, double[] vec2) {
            double dotProduct = 0;
            for (int i = 0; i < vec1.length; i++) {
                dotProduct += vec1[i] * vec2[i];
            }
            return dotProduct;
        }

        // Function to calculate magnitude of a vector
        public static double magnitude(double[] vec) {
            double sum = 0;
            for (double v : vec) {
                sum += v * v;
            }
            return Math.sqrt(sum);
        }

        // Function to calculate cosine similarity between two embedding vectors
        public static double cosineSimilarity(double[] vec1, double[] vec2) {
            double dotProduct = dotProduct(vec1, vec2);
            double magnitudeVec1 = magnitude(vec1);
            double magnitudeVec2 = magnitude(vec2);

            if (magnitudeVec1 == 0 || magnitudeVec2 == 0) {
                return 0; // To avoid division by zero
            } else {
                return dotProduct / (magnitudeVec1 * magnitudeVec2);
            }
        }

}
