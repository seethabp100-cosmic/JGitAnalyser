package com.mini.git.analyser.JGitAnalyser.service.impl;

import com.mini.git.analyser.JGitAnalyser.dto.request.AIFileRequest;
import com.mini.git.analyser.JGitAnalyser.dto.response.CommitChangeResponse;
import com.mini.git.analyser.JGitAnalyser.dto.response.CommitResponse;
import com.mini.git.analyser.JGitAnalyser.dto.response.FileChangeResponse;
import com.mini.git.analyser.JGitAnalyser.dto.response.JGitResponse;
import com.mini.git.analyser.JGitAnalyser.mapper.MongoDocumentMapper;
import com.mini.git.analyser.JGitAnalyser.mongodb.document.AIAnalysisDocument;
import com.mini.git.analyser.JGitAnalyser.mongodb.document.CommitDocument;
import com.mini.git.analyser.JGitAnalyser.mongodb.document.FileChangeDocument;
import com.mini.git.analyser.JGitAnalyser.mongodb.document.RepoDocument;
import com.mini.git.analyser.JGitAnalyser.mongodb.repo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class GitAnalysisPersistenceService {

    private static final Logger logger = LoggerFactory.getLogger(GitAnalysisPersistenceService.class);

    @Autowired
    private AIAnalysisRepository aiAnalysisRepository;
    @Autowired
    private CodeChangeRepository codeChangeRepository;
    @Autowired
    private FileChangeRepository fileChangeRepository;
    @Autowired
    private CommitRepository commitRepository;
    @Autowired
    private JGitMongoRepository jGitMongoRepository;
    @Autowired
    private MongoDocumentMapper mongoDocumentMapper;

    public GitAnalysisPersistenceService() {

    }

//    public GitAnalysisPersistenceService(AIAnalysisRepository aiAnalysisRepository, CodeChangeRepository codeChangeRepository, FileChangeRepository fileChangeRepository, CommitRepository commitRepository, JGitMongoRepository jGitMongoRepository, MongoDocumentMapper mongoDocumentMapper) {
//        this.aiAnalysisRepository = aiAnalysisRepository;
//        this.codeChangeRepository = codeChangeRepository;
//        this.fileChangeRepository = fileChangeRepository;
//        this.commitRepository = commitRepository;
//        this.jGitMongoRepository = jGitMongoRepository;
//        this.mongoDocumentMapper = mongoDocumentMapper;
//    }

    public String saveAnalysis(JGitResponse response, String repoUrl, String branch){
        String analysisId = UUID.randomUUID().toString();
        RepoDocument repoDocument = mongoDocumentMapper.toAnalysisRun(response, repoUrl, branch, analysisId);

        jGitMongoRepository.save(repoDocument);

        //save commits
        List<CommitDocument> commitDocuments = new ArrayList<>();

        for(CommitResponse commitResponse: response.getCommits() ){
          //  CommitChangeResponse commitChangeResponse = commitResponse.getCommitChangesResponse();
            CommitDocument commitDocument = mongoDocumentMapper.toCommitDocument(commitResponse, analysisId, repoUrl, branch);
            commitDocuments.add(commitDocument);

            //save files
            for(FileChangeResponse fileChangeResponse : commitResponse.getFiles()){

                FileChangeDocument fileChangeDocument = mongoDocumentMapper.toFileChangeDocument(fileChangeResponse, analysisId, commitResponse.getCommitId());
                fileChangeRepository.save(fileChangeDocument);
            }


            //save AI summary
            if(commitResponse.getAiAnalysis()!=null){
                AIAnalysisDocument aiAnalysisDocument = mongoDocumentMapper.toAiAnalysisDocument(commitResponse.getAiAnalysis(), analysisId, commitResponse.getCommitId());
                aiAnalysisRepository.save(aiAnalysisDocument);
            }
        }
        commitRepository.saveAll(commitDocuments);
        return analysisId;
    }


}
