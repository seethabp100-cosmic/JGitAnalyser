package com.mini.git.analyser.JGitAnalyser.dto.request;

import jakarta.validation.constraints.NotBlank;

public record JGitRequest(
        @NotBlank(message = "git url should not be null")
        String gitUrl,
        @NotBlank(message = "git branch should not be null")
        String branch,
        boolean includeAIAnalysis
) {
}
