package com.mini.git.analyser.JGitAnalyser.mongodb.repo;

import com.mini.git.analyser.JGitAnalyser.mongodb.document.CommitDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommitRepository extends MongoRepository<CommitDocument, String> {

}
