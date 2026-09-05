package com.mini.git.analyser.JGitAnalyser.service.impl;

import com.mini.git.analyser.JGitAnalyser.dto.response.CommitChangeResponse;
import com.mini.git.analyser.JGitAnalyser.dto.response.CommitResponse;
import com.mini.git.analyser.JGitAnalyser.service.CommitAnalysisService;
import com.mini.git.analyser.JGitAnalyser.service.DiffAnalysisService;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class CommitAnalysisServiceImpl implements CommitAnalysisService {


    @Autowired
    private DiffAnalysisService diffAnalysisService;

    @Override
    public List<CommitResponse> analyseCommits(Repository repository, String branch, int limits) throws Exception {
        List<CommitResponse> commitResponseList = new ArrayList<>();
        ObjectId branchId = resolveBranch(repository, branch);
        if (branchId == null) {
            throw new IllegalArgumentException("Branch not found: " + branchId);
        }

        try (RevWalk revWalk = new RevWalk(repository)) {
            RevCommit revCommitStart = revWalk.parseCommit(branchId);
            revWalk.markStart(revCommitStart);
            for (RevCommit revCommit : revWalk) {
                CommitResponse changes = diffAnalysisService.analyzeCommit(repository, revCommit);
                commitResponseList.add(changes);
                if (commitResponseList.size() >= limits) {
                    break;
                }
            }

        }


        return commitResponseList;
    }

    private ObjectId resolveBranch(Repository repository, String branch) throws IOException {
        ObjectId branchId = repository.resolve("refs/remotes/origin/" + branch);
        if (branchId == null) {
            branchId = repository.resolve("refs/heads/" + branch);
        }
        return branchId;
    }

    private String getHashCode(RevCommit revCommit) {
        return revCommit.getName().substring(0, 7);
    }
}
