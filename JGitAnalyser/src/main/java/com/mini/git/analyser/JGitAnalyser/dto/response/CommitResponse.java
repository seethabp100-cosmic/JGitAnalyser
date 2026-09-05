package com.mini.git.analyser.JGitAnalyser.dto.response;

import lombok.*;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CommitResponse{
    String commitId;
    String commitHash;
    String shortHash;
    Instant commitedAt;
    String message;
    String authorName;
    String authorEmail;
    String parentCommitId;
    int fileChanges;
    int additions;
    int deletions;
    List<FileChangeResponse> files;

    AIAnalysisResponse aiAnalysis;

}
