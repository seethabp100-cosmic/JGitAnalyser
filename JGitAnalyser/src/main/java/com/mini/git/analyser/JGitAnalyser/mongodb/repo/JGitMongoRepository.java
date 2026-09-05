package com.mini.git.analyser.JGitAnalyser.mongodb.repo;

import com.mini.git.analyser.JGitAnalyser.mongodb.document.RepoDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JGitMongoRepository extends MongoRepository<RepoDocument, String> {
}
