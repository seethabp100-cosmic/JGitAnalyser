package com.mini.git.analyser.JGitAnalyser.service;

import com.mini.git.analyser.JGitAnalyser.dto.response.JGitResponse;

public interface JGitAnalyserService {

    public JGitResponse analyze(String gitUrl, String branch, boolean includeAIAnalysis);


}
