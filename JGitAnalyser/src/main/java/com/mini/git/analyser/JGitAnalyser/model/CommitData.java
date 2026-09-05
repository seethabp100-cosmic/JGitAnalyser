package com.mini.git.analyser.JGitAnalyser.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommitData {

    String commitHash;
    String shortHash;
    String message;
    String authorName;
    String authorEmail;
    Instant analysedAt;
}
