package com.mini.git.analyser.JGitAnalyser.config;

import com.google.genai.Client;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GeminiConfig {

    @Bean
    public Client geminiClient(){
        String apiKey = System.getenv("GOOGLE_API_KEY");
        if(apiKey == null && apiKey.isBlank()){
            throw new IllegalArgumentException("Environment Variable: GOOGLE_API_KEY not configured");
        }
        return Client.builder().apiKey(apiKey).build();
    }

}
