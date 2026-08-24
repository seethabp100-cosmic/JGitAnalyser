package com.mini.git.analyser.JGitAnalyser.mongodb;

import com.mini.git.analyser.JGitAnalyser.dto.JGitCommitResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "git_analyser")
public class RepoDocument {
    @Id
    private String id;
    private String gitUrl;
    private String owner;
    private String repositoryName;
    private String branch;
    private int totalCommits;
    private CommitData latestCommit;
    private Instant analysedAt;
    private List<CommitData> recentCommits;

}
