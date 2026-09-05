package com.mini.git.analyser.JGitAnalyser.mongodb.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Document(collection = "git_ai_analyses")
public class AIAnalysisDocument {
    @Id
    private String aiAnalysisId;

    private String analysisId;

    @Indexed(unique = true)
    private String commitId;

    private String summary;
    private String changeType;
    private String impact;
    private String risk;

    private List<String> keyChanges;
    private List<String> potentialIssues;
    private List<String> reviewSuggestions;
}
