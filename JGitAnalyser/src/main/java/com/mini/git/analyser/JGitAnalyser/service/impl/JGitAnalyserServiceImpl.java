package com.mini.git.analyser.JGitAnalyser.service.impl;

import com.mini.git.analyser.JGitAnalyser.dto.analysis.AnalysisSummary;
import com.mini.git.analyser.JGitAnalyser.dto.context.RepositoryContext;
import com.mini.git.analyser.JGitAnalyser.dto.response.CommitResponse;
import com.mini.git.analyser.JGitAnalyser.dto.response.JGitCommitResponse;
import com.mini.git.analyser.JGitAnalyser.dto.response.JGitResponse;
import com.mini.git.analyser.JGitAnalyser.mapper.AIAnalysisMapper;
import com.mini.git.analyser.JGitAnalyser.model.CommitData;
import com.mini.git.analyser.JGitAnalyser.mongodb.repo.JGitMongoRepository;
import com.mini.git.analyser.JGitAnalyser.service.CommitAnalysisService;
import com.mini.git.analyser.JGitAnalyser.service.DiffAnalysisService;
import com.mini.git.analyser.JGitAnalyser.service.JGitAnalyserService;
import com.mini.git.analyser.JGitAnalyser.service.util.JGitAnalyzeServiceUtil;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
@NoArgsConstructor
@AllArgsConstructor
public class JGitAnalyserServiceImpl implements JGitAnalyserService {

    private static final Logger logger = LoggerFactory.getLogger(JGitAnalyserServiceImpl.class);

    @Autowired
    private JGitAnalyzeServiceUtil gitAnalyzeUtil;

    @Autowired
    private DiffAnalysisService diffAnalysisService;

    @Autowired
    private CommitAnalysisService commitAnalysisService;

    @Autowired
    private JGitMongoRepository mongoRepository;

    @Autowired
    private GitAnalysisPersistenceService persistenceService;

    @Autowired
    private AIAnalysisMapper aiAnalysisMapper;

    @Autowired
    AIAnalysisServiceImpl aiAnalysisService;

    @Override
    public JGitResponse analyze(String gitUrl, String branch, boolean includeAIAnalysis) {
        logger.info("[Service] Start: Git Repo Analysis");
        Path tempDirectory = null;
        try {
            //clone the repo
            tempDirectory = Files.createTempDirectory("git-analyser-");
            logger.info("[Service] temporary Directory: {}", tempDirectory);

            RepositoryContext context = gitAnalyzeUtil.cloneRepository(gitUrl, branch, tempDirectory);
            logger.info("[Service] Repository Context:: {}",context);
            List<CommitResponse> responseList = commitAnalysisService.analyseCommits(context.repository(), branch, 20);
            logger.info("[Service] Analysed Commit List:: {}", responseList);
            CommitResponse commitResponse = responseList.isEmpty()? null : responseList.get(0);

            JGitResponse response =  buildResponse(gitUrl, branch, context, responseList, commitResponse);
            logger.info("[Service] Analyzed Full Git Response:: {}", response);
            if(includeAIAnalysis){
                aiAnalysisService.enrichAnalysisWithAI(response);
            }

            persistenceService.saveAnalysis(response, gitUrl, branch);

            return response;
            /*try (Git git = Git.cloneRepository()
                    .setURI(gitUrl)
                    .setDirectory(tempDirectory.toFile())
                    .setCloneAllBranches(true).call()) {


                logger.info("Git uri::{} clone completed", gitUrl);

                //checkout to branch
                checkoutToBranch(git, branch);

                //extract repoName from repo
                Repository repository = git.getRepository();
                String owner = gitAnalyzeUtil.extractOwnerName(gitUrl);
                logger.info("[Service] Owner Name: {}",owner);
                //extract repo owner name
                String repoName = gitAnalyzeUtil.extractRepoName(gitUrl);
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
            } */


            //extract branch from repo
        } catch (Exception e) {
            logger.error("Error, Failed to Analyze Git Repo : {}",e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private JGitResponse buildResponse(
            String gitUrl,
            String branch,
            RepositoryContext context,
            List<CommitResponse> commits,
            CommitResponse latestCommit) {

        int totalAdditions =
                commits.stream()
                        .mapToInt(CommitResponse::getAdditions)
                        .sum();

        int totalDeletions =
                commits.stream()
                        .mapToInt(CommitResponse::getDeletions)
                        .sum();

        int filesChanged =
                commits.stream()
                        .mapToInt(CommitResponse::getFileChanges)
                        .sum();

        int filesAdded = commits.stream()
                .flatMap(c -> c.getFiles().stream())
                .filter(file ->file.getChangeType().equals("ADD") )
                .mapToInt(fileChanged -> 1).sum();

        int filesModified = commits.stream()
                .flatMap(c -> c.getFiles().stream())
                .filter(file -> file.getChangeType().equals("MODIFY"))
                .mapToInt(file -> 1)
                .sum();
        AnalysisSummary summary =
                new AnalysisSummary(
                        filesChanged,
                        filesAdded,
                        filesModified,
                        0,
                        0,
                        totalAdditions,
                        totalDeletions
                );

        return new JGitResponse(
                context.gitUrl(),
                branch,
                commits.size(),
                filesChanged,
                totalAdditions,
                totalDeletions,
                commits
        );
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
            logger.info("Error, Failed to branch: {} checkout ", branch);
            throw new RuntimeException(e);
        }
    }
}
