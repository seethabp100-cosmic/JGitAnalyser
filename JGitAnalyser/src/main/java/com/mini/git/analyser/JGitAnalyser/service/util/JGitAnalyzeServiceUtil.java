package com.mini.git.analyser.JGitAnalyser.service.util;

import com.mini.git.analyser.JGitAnalyser.dto.context.RepositoryContext;
import com.mini.git.analyser.JGitAnalyser.dto.request.AIFileRequest;
import com.mini.git.analyser.JGitAnalyser.dto.response.FileChangeResponse;
import com.mini.git.analyser.JGitAnalyser.model.CommitData;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
public class JGitAnalyzeServiceUtil {

    private static final Logger logger = LoggerFactory.getLogger(JGitAnalyzeServiceUtil.class);

    public List<CommitData> extractCommitDetails(Repository repository, String branch)
            throws Exception {
        logger.info("Extracting Commit details:: Start");
        List<CommitData> commitData = new ArrayList<>();
        ObjectId branchId = repository.resolve(branch);
        if (branchId == null) {
            branchId = repository.resolve("/refs/remotes/origin/" + branch);
        }

        if (branchId == null) {
            throw new IllegalArgumentException("Unable to Resolve the branch: " + branch);
        }

        try (RevWalk revWalk = new RevWalk(repository)) {
            // RevCommit commit = revWalk.parseCommit(branchId);
            revWalk.markStart(
                    revWalk.parseCommit(branchId)
            );
            for (RevCommit curCommit : revWalk) {
                commitData.add(CommitData.builder().commitHash(curCommit.getName()).
                        shortHash(curCommit.getName().substring(0, 7))
                        .message(curCommit.getShortMessage()).authorEmail(curCommit.getAuthorIdent()
                                .getEmailAddress()).authorName(curCommit.getAuthorIdent().getName())
                        .analysedAt(curCommit.getAuthorIdent().getWhen().toInstant()).build());

                if (commitData.size() >= 20) {
                    break;
                }
            }

        }
        return commitData;
    }

    public String extractRepoName(String gitUrl) {
        String value = gitUrl.substring(gitUrl.lastIndexOf("/"));

        if (value.endsWith(".git")) {
            value = value.substring(0, value.length() - 4);
        }
        return value;
    }

    public String extractOwnerName(String gitUrl) {

        String cleanUri = gitUrl.endsWith(".git") ? gitUrl.substring(0, gitUrl.length() - 4) : gitUrl;

        String[] parts = cleanUri.split("/");

        return parts[parts.length - 2];
    }

    public void deleteTempDir(Path directory) {
        try {
            Files.walk(directory).sorted(Comparator.reverseOrder()).forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException e) {
                    logger.warn("unable to delete: {}", path);
                }
            });
        } catch (Exception e) {
            logger.error("unable to cleanup temporary repository:: {}", e);
        }

    }

    public RepositoryContext cloneRepository(
            String gitUrl,
            String branch,
            Path tempDirectory) {

        try {
            Git git = Git.cloneRepository()
                    .setURI(gitUrl)
                    .setDirectory(tempDirectory.toFile())
                    .setCloneAllBranches(true)
                    .call();

            Repository repository = git.getRepository();

            ObjectId branchId = repository.resolve(
                    "refs/remotes/origin/" + branch
            );

            if (branchId == null) {
                git.close();

                throw new IllegalArgumentException(
                        "Branch not found: " + branch
                );
            }

            return new RepositoryContext(
                    repository,
                    gitUrl,
                    extractOwnerName(gitUrl),
                    extractRepoName(gitUrl),
                    branch
            );

        } catch (IOException | GitAPIException e) {
            logger.error("Failed to clone Git repository: {}", gitUrl, e);
        }
        return null;
    }

    public AIFileRequest toAIFile(FileChangeResponse fileChangeResponse) {
        AIFileRequest aiFileRequest = new AIFileRequest();
        aiFileRequest.setFilePath(fileChangeResponse.getFilePath());
        aiFileRequest.setChangeType(fileChangeResponse.getChangeType());
        aiFileRequest.setAdditions(fileChangeResponse.getAdditions());
        aiFileRequest.setDeletions(fileChangeResponse.getDeletions());
        aiFileRequest.setCodeChange(fileChangeResponse.getCodeChange());
        return aiFileRequest;
    }
}
