package com.mini.git.analyser.JGitAnalyser.service;


import com.mini.git.analyser.JGitAnalyser.dto.response.CommitResponse;
import org.eclipse.jgit.lib.Repository;

import java.util.List;

public interface CommitAnalysisService {

    public List<CommitResponse> analyseCommits(Repository repository, String branch, int limits) throws Exception;

}
