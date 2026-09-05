package com.mini.git.analyser.JGitAnalyser.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mini.git.analyser.JGitAnalyser.client.GeminiAIClient;
import com.mini.git.analyser.JGitAnalyser.dto.request.AICommitRequest;
import com.mini.git.analyser.JGitAnalyser.dto.response.AIAnalysisResponse;
import com.mini.git.analyser.JGitAnalyser.dto.response.CommitResponse;
import com.mini.git.analyser.JGitAnalyser.dto.response.JGitResponse;
import com.mini.git.analyser.JGitAnalyser.mapper.AIAnalysisMapper;
import com.mini.git.analyser.JGitAnalyser.service.util.AIPromptBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AIAnalysisServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(AIAnalysisServiceImpl.class);

    @Autowired
    private GeminiAIClient geminiAIClient;

    @Autowired
    private AIPromptBuilder aiPromptBuilder;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    AIAnalysisMapper aiAnalysisMapper;

    public AIAnalysisResponse analyseSummaryWithAI(String repository, CommitResponse commitResponse) {

        AICommitRequest commitRequest = aiAnalysisMapper.toAICommitRequest(repository, commitResponse);

        String prompt = aiPromptBuilder.buildPrompt(commitRequest);

        try{
            String result = geminiAIClient.generateAnalysis(prompt);
            return objectMapper.readValue(result, AIAnalysisResponse.class);
        } catch (Exception e) {
            logger.error("Error, Failed to Analysing Response with AI:: {}", e.getMessage());
        }

        return  null;
    }

    //AI analysis for the git summary
    public void enrichAnalysisWithAI(JGitResponse response){
        try{
            for (CommitResponse commitResponse: response.getCommits()){
                try{
                    AIAnalysisResponse aiAnalysisResponse = analyseSummaryWithAI(response.getRepository(), commitResponse);
                    commitResponse.setAiAnalysis(aiAnalysisResponse);
                } catch (Exception e) {

                }
            }
        } catch (Exception e) {
            logger.error("Error, Failed to enrich the AI Analysis:: {}", e.getMessage());
        }
    }
}
