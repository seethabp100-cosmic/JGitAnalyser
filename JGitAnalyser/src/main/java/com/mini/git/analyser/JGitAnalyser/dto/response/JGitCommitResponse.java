package com.mini.git.analyser.JGitAnalyser.dto.response;

import java.time.Instant;

public record JGitCommitResponse (
        String commitHash,
        String shortHash,
        String message,
        String authorName,
        String authorEmail,
        Instant commitedAt
){
}
