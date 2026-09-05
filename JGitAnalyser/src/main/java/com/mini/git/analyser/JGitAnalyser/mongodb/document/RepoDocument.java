package com.mini.git.analyser.JGitAnalyser.mongodb.document;

import com.mini.git.analyser.JGitAnalyser.model.CommitData;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Document(collection = "git_analyses_run")
public class RepoDocument {
    @Id
    private String analysisId;
    private String gitUrl;
    private String owner;
    private String repositoryName;
    private String branch;
    private Instant analysedAt;
    private int totalCommits;
    private int totalFilesChanged;
    private int totalAdditions;
    private int totalDeletions;

}
