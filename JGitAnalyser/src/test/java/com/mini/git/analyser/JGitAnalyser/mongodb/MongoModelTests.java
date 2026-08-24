package com.mini.git.analyser.JGitAnalyser.mongodb;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MongoModelTests {

    @Test
    void commitData_and_repoDocument_builder_and_getters() {
        CommitData cd = CommitData.builder()
                .commitHash("h1")
                .shortHash("s1")
                .message("m")
                .authorName("a")
                .authorEmail("e")
                .analysedAt(Instant.now())
                .build();
        assertEquals("h1", cd.getCommitHash());

        RepoDocument rd = RepoDocument.builder()
                .gitUrl("u")
                .owner("o")
                .repositoryName("r")
                .branch("b")
                .totalCommits(1)
                .latestCommit(cd)
                .recentCommits(List.of(cd))
                .build();

        assertEquals("u", rd.getGitUrl());
        assertEquals(cd, rd.getLatestCommit());
    }
}
