package com.mini.git.analyser.JGitAnalyser.dto.response;

import com.mini.git.analyser.JGitAnalyser.dto.request.AIFileRequest;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CommitChangeResponse {
    String commitId;
    String parentCommitId;

    String author;
    String message;
    Instant date;

    int fileChanges;
    int additions;
    int deletions;
    List<FileChangeResponse> files;

    AIAnalysisResponse aiAnalysis;
}
