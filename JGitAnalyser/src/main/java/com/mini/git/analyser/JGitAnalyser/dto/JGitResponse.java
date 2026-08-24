package com.mini.git.analyser.JGitAnalyser.dto;

import java.util.List;

public record JGitResponse(
        String analysisId,
        String owner,
        String repoName,
        String branch,
        int totalCommits,
        JGitCommitResponse latestCommit,
        List<JGitCommitResponse> recentCommits
) {
}
