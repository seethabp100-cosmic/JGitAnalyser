package com.mini.git.analyser.JGitAnalyser.mapper;

import com.mini.git.analyser.JGitAnalyser.dto.response.*;
import com.mini.git.analyser.JGitAnalyser.model.CommitData;
import com.mini.git.analyser.JGitAnalyser.mongodb.document.*;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

@Component
public class MongoDocumentMapper {

    public RepoDocument toAnalysisRun(
            JGitResponse gitAnalysisResponse,
            String repoUrl,
            String branch,
            String analysisId
    ){
        RepoDocument repoDocument = new RepoDocument();
        repoDocument.setAnalysisId(analysisId);
        repoDocument.setGitUrl(repoUrl);
        repoDocument.setBranch(branch);
        repoDocument.setOwner(gitAnalysisResponse.getAuthor());
        repoDocument.setAnalysedAt(Instant.now());
        repoDocument.setTotalCommits(gitAnalysisResponse.getCommits().size());

        repoDocument.setTotalFilesChanged(gitAnalysisResponse.getTotalFilesChanged());

//        repoDocument.setTotalAdditions(
//                gitAnalysisResponse.getCommits().stream().map(CommitResponse::getCommitChangesResponse)
//                        .filter(Objects::nonNull)
//                        .mapToInt(CommitChangeResponse::getAdditions).sum()
//        );

        repoDocument.setTotalAdditions(
                gitAnalysisResponse.getTotalAdditions()
        );
        repoDocument.setTotalDeletions(
                gitAnalysisResponse.getTotalDeletions()
        );
        return repoDocument;
    }

    public CommitDocument toCommitDocument(
            CommitResponse commitChangeResponse,
            String analysisId,
            String gitUrl,
            String branch
    ){
        CommitDocument commitDocument = new CommitDocument();

        commitDocument.setCommitId(commitDocument.getCommitId());
        commitDocument.setBranch(branch);
        commitDocument.setRepositoryUrl(gitUrl);
        commitDocument.setAnalysisId(analysisId);
        commitDocument.setParentCommitId(commitChangeResponse.getParentCommitId());
        commitDocument.setAuthor(commitChangeResponse.getAuthorName());
        commitDocument.setMessage(commitChangeResponse.getMessage());
        commitDocument.setCommittedAt(commitChangeResponse.getCommitedAt());
        commitDocument.setAdditions(commitChangeResponse.getAdditions());
        commitDocument.setDeletions(commitChangeResponse.getDeletions());

        return commitDocument;
    }

    public FileChangeDocument toFileChangeDocument(
        FileChangeResponse fileChangeResponse,
        String analysisId,
        String commitId
    ){

        FileChangeDocument fileChangeDocument = new FileChangeDocument();
        fileChangeDocument.setAnalysisId(analysisId);
        fileChangeDocument.setCommitId(commitId);
        fileChangeDocument.setChangeType(fileChangeDocument.getChangeType());
        fileChangeDocument.setFilePath(fileChangeResponse.getFilePath());
        fileChangeDocument.setFileChangeId(fileChangeDocument.getFileChangeId());
        fileChangeDocument.setAdditions(fileChangeResponse.getAdditions());
        fileChangeDocument.setDeletions(fileChangeResponse.getDeletions());
        fileChangeDocument.setCodeChange(fileChangeResponse.getCodeChange());
        return fileChangeDocument;
    }

    public AIAnalysisDocument toAiAnalysisDocument(
            AIAnalysisResponse aiAnalysisResponse,
            String analysisId,
            String commitId
    ){
        AIAnalysisDocument aiAnalysisDocument = new AIAnalysisDocument();

        aiAnalysisDocument.setAiAnalysisId(UUID.randomUUID().toString());
        aiAnalysisDocument.setAnalysisId(analysisId);
        aiAnalysisDocument.setCommitId(commitId);
        aiAnalysisDocument.setSummary(aiAnalysisResponse.getSummary());
        aiAnalysisDocument.setImpact(aiAnalysisResponse.getImpact());
        aiAnalysisDocument.setRisk(aiAnalysisResponse.getRisk());
        aiAnalysisDocument.setPotentialIssues(aiAnalysisResponse.getPotentialIssues());
        aiAnalysisDocument.setKeyChanges(aiAnalysisResponse.getKeyChanges());
        aiAnalysisDocument.setChangeType(aiAnalysisResponse.getChangeType());
        aiAnalysisDocument.setReviewSuggestions(aiAnalysisResponse.getReviewSuggestions());

        return aiAnalysisDocument;
    }



}
