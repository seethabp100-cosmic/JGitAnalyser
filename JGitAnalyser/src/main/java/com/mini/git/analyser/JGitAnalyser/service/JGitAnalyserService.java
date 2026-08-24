package com.mini.git.analyser.JGitAnalyser.service;

import com.mini.git.analyser.JGitAnalyser.dto.JGitResponse;

public interface JGitAnalyserService {

    public JGitResponse analyze(String gitUrl, String branch);
}
