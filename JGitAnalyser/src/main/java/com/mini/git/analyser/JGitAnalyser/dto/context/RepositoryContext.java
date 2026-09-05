package com.mini.git.analyser.JGitAnalyser.dto.context;

import org.eclipse.jgit.lib.Repository;

public record RepositoryContext(
        Repository repository,
        String gitUrl,
        String owner,
        String repoName,
        String branch
) {
}
