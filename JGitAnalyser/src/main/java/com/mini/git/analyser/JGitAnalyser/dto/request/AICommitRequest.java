package com.mini.git.analyser.JGitAnalyser.dto.request;

import lombok.*;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AICommitRequest {
    private String repository;
    private String commitId;
    private String message;
    private String author;
    private Instant date;

    private int filesChanged;
    private int additions;
    private int deletions;
    List<AIFileRequest> files;
}
