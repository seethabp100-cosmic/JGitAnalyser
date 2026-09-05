package com.mini.git.analyser.JGitAnalyser.mapper;

import com.mini.git.analyser.JGitAnalyser.dto.request.AICommitRequest;
import com.mini.git.analyser.JGitAnalyser.dto.request.AIFileRequest;
import com.mini.git.analyser.JGitAnalyser.dto.response.*;
import com.mini.git.analyser.JGitAnalyser.service.impl.AIAnalysisServiceImpl;
import com.mini.git.analyser.JGitAnalyser.service.util.JGitAnalyzeServiceUtil;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.lib.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class AIAnalysisMapper {

    @Autowired
    JGitAnalyzeServiceUtil jGitAnalyzeServiceUtil;

    @Autowired
    MongoDocumentMapper mapper;

    public AICommitRequest toAICommitRequest(String repository, CommitResponse commitChangeResponse){

        AICommitRequest aiCommitRequest = new AICommitRequest();
        aiCommitRequest.setRepository(repository);
        aiCommitRequest.setCommitId(commitChangeResponse.getCommitId());
        aiCommitRequest.setAuthor(commitChangeResponse.getAuthorName());
        aiCommitRequest.setMessage(commitChangeResponse.getMessage());
        aiCommitRequest.setDate(commitChangeResponse.getCommitedAt());

        aiCommitRequest.setFilesChanged(commitChangeResponse.getFileChanges());
        aiCommitRequest.setAdditions(commitChangeResponse.getAdditions());
        aiCommitRequest.setDeletions(commitChangeResponse.getDeletions());

        List<AIFileRequest> aiFiles = new ArrayList<>();
        for(FileChangeResponse response: commitChangeResponse.getFiles()){
            AIFileRequest aiFile = jGitAnalyzeServiceUtil.toAIFile(response);
            aiFiles.add(aiFile);
        }
        aiCommitRequest.setFiles(
                aiFiles
        );

        return aiCommitRequest;
    }

}
