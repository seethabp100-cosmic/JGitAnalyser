package com.mini.git.analyser.JGitAnalyser.dto;

import com.mini.git.analyser.JGitAnalyser.dto.response.CommitResponse;
import com.mini.git.analyser.JGitAnalyser.dto.request.JGitRequest;
import com.mini.git.analyser.JGitAnalyser.dto.response.JGitResponse;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DtoTests {

    @Test
    void jgitRequest_and_commit_and_response_work() {
        JGitRequest req = new JGitRequest("https://x/y.git", "main", true);
        assertEquals("https://x/y.git", req.gitUrl());
        assertEquals("main", req.branch());

        //JGitCommitResponse commit = new JGitCommitResponse("hash","short","msg","name","e@x.com", Instant.now());
        CommitResponse commit = new CommitResponse("commitId", "hash","short",Instant.now(),"message","name","e@x.com","parrentid",0,0,0,List.of(), null);

        assertEquals("hash", commit.getCommitHash());

        JGitResponse resp = new JGitResponse("repo","main",1,1,1,1,List.of(commit));
        //assertEquals("id", resp.());
        assertEquals(1, resp.getTotalCommits());
    }
}
