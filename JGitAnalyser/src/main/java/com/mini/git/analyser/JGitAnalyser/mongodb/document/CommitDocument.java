package com.mini.git.analyser.JGitAnalyser.mongodb.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Document(collection = "git_commits")
@CompoundIndex(
        name = "repository_branch_date_idx",
        def = "{'repositoryUrl': 1, 'branch': 1, 'committedAt': -1}"
)
public class CommitDocument {
    @Id
    private String commitId;

    private String analysisId;

    private String repositoryUrl;
    private String branch;

    private String parentCommitId;

    private String author;
    private String message;

    private Instant committedAt;

    private int filesChanged;
    private int additions;
    private int deletions;

}
