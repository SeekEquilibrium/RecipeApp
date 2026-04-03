package com.example.praksa.Config;

import com.example.praksa.Utills.RecipeCsvRepresentation;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import jakarta.annotation.PostConstruct;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Map;

@Component
public class RecipeLoader {

    private final VectorStore vectorStore;

    @Value("${app.vector-store.reload:false}")
    private boolean reload;

    public RecipeLoader(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @PostConstruct
    public void load() throws IOException {
        if (!reload) {
            return;
        }

        ClassPathResource resource = new ClassPathResource("Food_Recipe.csv");
        try (Reader reader = new InputStreamReader(resource.getInputStream())) {
            HeaderColumnNameMappingStrategy<RecipeCsvRepresentation> strategy = new HeaderColumnNameMappingStrategy<>();
            strategy.setType(RecipeCsvRepresentation.class);

            CsvToBean<RecipeCsvRepresentation> csvToBean = new CsvToBeanBuilder<RecipeCsvRepresentation>(reader)
                    .withMappingStrategy(strategy)
                    .withIgnoreEmptyLine(true)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            List<Document> documents = csvToBean.parse().stream()
                    .map(row -> {
                        String content = String.format(
                                "Recipe: %s\nCuisine: %s\nCourse: %s\nDiet: %s\n" +
                                "Prep: %s mins | Cook: %s mins\n" +
                                "Ingredients: %s\nQuantities: %s\n" +
                                "Instructions: %s",
                                row.getName(), row.getCuisine(), row.getCourse(), row.getDiet(),
                                row.getPrepTime(), row.getCookTime(),
                                row.getIngredientsName(), row.getIngredientsQuantity(),
                                row.getInstructions()
                        );
                        Map<String, Object> metadata = Map.of(
                                "name", row.getName() != null ? row.getName() : "",
                                "cuisine", row.getCuisine() != null ? row.getCuisine() : "",
                                "imageUrl", row.getImageUrl() != null ? row.getImageUrl() : ""
                        );
                        return new Document(content, metadata);
                    })
                    .toList();

            int batchSize = 10;
            for (int i = 0; i < documents.size(); i += batchSize) {
                List<Document> batch = documents.subList(i, Math.min(i + batchSize, documents.size()));
                vectorStore.add(batch);
                if (i + batchSize < documents.size()) {
                    try {
                        Thread.sleep(15000); // 15s pause keeps us well under 40K TPM
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }
    }
}