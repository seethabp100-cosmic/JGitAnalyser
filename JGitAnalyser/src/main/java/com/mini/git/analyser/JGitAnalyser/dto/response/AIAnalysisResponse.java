package com.mini.git.analyser.JGitAnalyser.dto.response;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AIAnalysisResponse{
         String summary;
         String changeType;
         String impact;
         String risk;

         List<String> keyChanges;
          List<String> potentialIssues;
         List<String> reviewSuggestions;

}
