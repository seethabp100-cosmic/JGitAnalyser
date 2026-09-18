package com.mini.git.analyser.JGitAnalyser.dto.response;

import com.mini.git.analyser.JGitAnalyser.dto.analysis.AnalysisSummary;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class JGitResponse {
    /* * Repository information */
    private String repository;
    private String branch;
    private String author;
    /* * Overall analysis statistics */
    private int totalCommits;
    private int totalFilesChanged;
    private int totalAdditions;
    private int totalDeletions;
    private Instant analysedAt;
    /* * Commit-level analysis */
    private List<CommitResponse> commits;
}
