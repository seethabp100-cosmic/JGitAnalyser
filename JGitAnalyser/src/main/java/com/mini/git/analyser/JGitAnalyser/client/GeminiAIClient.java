package com.mini.git.analyser.JGitAnalyser.client;

import com.google.common.collect.ImmutableMap;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Schema;
import com.google.genai.types.Type;
import com.mini.git.analyser.JGitAnalyser.properties.GeminiProperties;
import org.springframework.stereotype.Component;

@Component
public class GeminiAIClient {

    private GeminiProperties geminiProperties;
    private Client client;

    public GeminiAIClient(GeminiProperties geminiProperties, Client client) {
        this.geminiProperties = geminiProperties;
        this.client = client;
    }

    public String generateAnalysis(String prompt){
        Schema schema = buildResponseSchema();

        GenerateContentConfig contentConfig = GenerateContentConfig.builder().responseMimeType("application/json")
                .responseSchema(schema).candidateCount(1).build();

        GenerateContentResponse response = client.models.generateContent(geminiProperties.getModel(), prompt, contentConfig);

        String result = response.text();

        if(result == null && result.isBlank()){
            throw new IllegalArgumentException("Gemini returned an empty response");
        }

        return result;
    }

    public Schema buildResponseSchema(){

        Schema stringArray = Schema.builder().type(Type.Known.ARRAY).
                items(Schema.builder().type(Type.Known.STRING)).build();


        return Schema.builder().type(Type.Known.OBJECT).properties(
                ImmutableMap.of(
                        "summary", Schema.builder().type(Type.Known.STRING).build(),
                        "changeType", Schema.builder().type(Type.Known.STRING).build(),
                        "impact", Schema.builder().type(Type.Known.STRING).build(),
                        "risk", Schema.builder().type(Type.Known.STRING).build(),
                        "keyChanges", stringArray,
                        "potentialIssues", stringArray,
                        "reviewSuggestions", stringArray
                )).required(
                        "summary",
                        "changeType",
                        "impact",
                        "risk",
                        "keyChanges",
                        "potentialIssues",
                        "reviewSuggestions").build();

    }
}
