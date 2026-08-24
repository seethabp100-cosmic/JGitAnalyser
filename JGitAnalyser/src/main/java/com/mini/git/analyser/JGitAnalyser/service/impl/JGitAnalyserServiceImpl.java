package com.mini.git.analyser.JGitAnalyser.service.impl;

import com.mini.git.analyser.JGitAnalyser.dto.JGitCommitResponse;
import com.mini.git.analyser.JGitAnalyser.dto.JGitResponse;
import com.mini.git.analyser.JGitAnalyser.mongodb.CommitData;
import com.mini.git.analyser.JGitAnalyser.mongodb.JGitMongoRepository;
import com.mini.git.analyser.JGitAnalyser.mongodb.RepoDocument;
import com.mini.git.analyser.JGitAnalyser.service.JGitAnalyserService;
import com.mini.git.analyser.JGitAnalyser.service.util.JGitAnalyzeServiceUtil;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;

@Service
@NoArgsConstructor
@AllArgsConstructor
public class JGitAnalyserServiceImpl implements JGitAnalyserService {

    private static final Logger logger = LoggerFactory.getLogger(JGitAnalyserServiceImpl.class);

    @Autowired
    private JGitAnalyzeServiceUtil gitAnalyzeUtil;

    @Autowired
    private JGitMongoRepository mongoRepository;

    @Override
    public JGitResponse analyze(String gitUrl, String branch) {
        logger.info("[Service] Start: Git Repo Analysis");
        Path tempDirectory = null;
        try {
            //clone the repo
            tempDirectory = Files.createTempDirectory("git-analyser-");
            logger.info("[Service] temporary Directory: {}", tempDirectory);
            try (Git git = Git.cloneRepository()
                    .setURI(gitUrl)
                    .setDirectory(tempDirectory.toFile())
                    .setCloneAllBranches(true).call()) {


                logger.info("Git uri::{} clone completed", gitUrl);

                //checkout to branch
                checkoutToBranch(git, branch);

                //extract repoName from repo
                Repository repository = git.getRepository();
                String owner = gitAnalyzeUtil.extractRepoName(gitUrl);
                logger.info("[Service] Owner Name: {}",owner);
                //extract repo owner name
                String repoName = gitAnalyzeUtil.extractRepoOwnerName(gitUrl);
                logger.info("[Service] Repo Name: {}",repoName);
                //Read commits from Repo
                List<CommitData> latestCommits = gitAnalyzeUtil.extractCommitDetails(repository, branch);
                logger.info("[Service] Latest commits: {}",latestCommits);
                CommitData latestCommit = (latestCommits != null && latestCommits.isEmpty()) ? null : latestCommits.get(0);
                logger.info("[Service] Current commit: {}",latestCommit);
                //save in mongoDb
                RepoDocument document = RepoDocument.builder().gitUrl(gitUrl).owner(owner).branch(branch)
                        .repositoryName(repoName)
                        .totalCommits(latestCommits.size())
                        .latestCommit(latestCommit)
                        .recentCommits(latestCommits)
                        .analysedAt(Instant.now())
                        .build();

                //convert to API response

                RepoDocument savedDoc = mongoRepository.save(document);
                logger.info("[Service] Saved Document: {}",savedDoc);

                return new JGitResponse(
                        savedDoc.getId(),
                        savedDoc.getOwner(),
                        savedDoc.getRepositoryName(),
                        savedDoc.getBranch(),
                        savedDoc.getTotalCommits(),
                        toResponse(latestCommit),
                        latestCommits.stream().map(this::toResponse).toList()
                );
            }


            //extract branch from repo
        } catch (Exception e) {
            logger.error("Error, Failed to Analyze Git Repo : {}",e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private JGitCommitResponse toResponse(CommitData commitData) {
        if (commitData == null){
            return null;
        }

        return new JGitCommitResponse(
                commitData.getCommitHash(),
                commitData.getShortHash(),
                commitData.getMessage(),
                commitData.getAuthorName(),
                commitData.getAuthorEmail(),
                commitData.getAnalysedAt()
        );
    }

    private void checkoutToBranch(Git git, String branch) {
        String remoteBranch = "origin/" + branch;
        boolean branchExists = false;
        try {
            branchExists = git.branchList()
                    .setListMode(
                            ListBranchCommand.ListMode.REMOTE
                    ).call()
                    .stream()
                    .anyMatch(
                            ref ->
                                    ref.getName().equals(Constants.R_REMOTES + remoteBranch)
                    );
        } catch (GitAPIException e) {
            throw new RuntimeException(e);
        }

        if (!branchExists) {
            throw new IllegalArgumentException("Branch not found");
        }

        try {
            git.checkout().setCreateBranch(true).setName(branch).setStartPoint(remoteBranch).call();
        } catch (GitAPIException e) {
            throw new RuntimeException(e);
        }
    }
}
