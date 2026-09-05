package com.mini.git.analyser.JGitAnalyser.service;

import com.mini.git.analyser.JGitAnalyser.dto.response.CommitChangeResponse;
import com.mini.git.analyser.JGitAnalyser.dto.response.CommitResponse;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

import java.io.IOException;

public interface DiffAnalysisService {
    public CommitResponse analyzeCommit(Repository repository, RevCommit commit) throws IOException;
}
