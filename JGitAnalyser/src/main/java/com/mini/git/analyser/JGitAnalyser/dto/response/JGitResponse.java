package com.mini.git.analyser.JGitAnalyser.dto.response;

import com.mini.git.analyser.JGitAnalyser.dto.analysis.AnalysisSummary;
import lombok.*;

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
    /* * Overall analysis statistics */
    private int totalCommits;
    private int totalFilesChanged;
    private int totalAdditions;
    private int totalDeletions;
    /* * Commit-level analysis */
    private List<CommitResponse> commits;
}
