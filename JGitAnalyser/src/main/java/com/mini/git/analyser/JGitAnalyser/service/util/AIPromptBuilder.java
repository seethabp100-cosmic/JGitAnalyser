package com.mini.git.analyser.JGitAnalyser.service.util;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mini.git.analyser.JGitAnalyser.dto.request.AICommitRequest;
import org.springframework.stereotype.Component;

@Component
public class AIPromptBuilder {

    private final ObjectMapper objectMapper;

    public AIPromptBuilder(ObjectMapper objectMapper){
        this.objectMapper = objectMapper;
    }

    public String buildPrompt(AICommitRequest request){

        try{
            String gitData = objectMapper.writeValueAsString(request);

            return """
                    You are a senior software engineer
                    analyzing a Git commit.

                    Analyze only the Git information supplied below.

                    Do not invent functionality.
                    Do not assume requirements that are not present.
                    Do not claim an issue without evidence.

                    Analyze:

                    1. Purpose of the commit
                    2. Important technical changes
                    3. Change type
                    4. Technical impact
                    5. Potential risks
                    6. Potential implementation issues
                    7. Code review suggestions

                    changeType must be one of:

                    FEATURE
                    BUG_FIX
                    REFACTOR
                    CONFIGURATION
                    DOCUMENTATION
                    TEST
                    PERFORMANCE
                    SECURITY
                    OTHER

                    impact must be:

                    LOW
                    MEDIUM
                    HIGH

                    risk must be:

                    LOW
                    MEDIUM
                    HIGH

                    Return only JSON matching the requested schema.

                    Git commit:

                    %s
                    """.formatted(gitData);
        }catch(JsonProcessingException e){
            throw new IllegalArgumentException("Failed to create AI prompt: {}", e);
        }
    }

}
