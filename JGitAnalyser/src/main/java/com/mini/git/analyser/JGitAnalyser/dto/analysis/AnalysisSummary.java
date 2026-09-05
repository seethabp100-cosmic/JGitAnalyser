package com.mini.git.analyser.JGitAnalyser.dto.analysis;

public record AnalysisSummary(
        int filesChanged,

        int filesAdded,

        int filesModified,

        int filesDeleted,

        int filesRenamed,

        int additions,

        int deletions
) {
}
