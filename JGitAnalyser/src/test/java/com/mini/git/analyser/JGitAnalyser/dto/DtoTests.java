package com.mini.git.analyser.JGitAnalyser.dto;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DtoTests {

    @Test
    void jgitRequest_and_commit_and_response_work() {
        JGitRequest req = new JGitRequest("https://x/y.git", "main");
        assertEquals("https://x/y.git", req.gitUrl());
        assertEquals("main", req.branch());

        JGitCommitResponse commit = new JGitCommitResponse("hash","short","msg","name","e@x.com", Instant.now());
        assertEquals("hash", commit.commitHash());

        JGitResponse resp = new JGitResponse("id","owner","repo","main",1,commit, List.of(commit));
        assertEquals("id", resp.analysisId());
        assertEquals(1, resp.totalCommits());
    }
}
