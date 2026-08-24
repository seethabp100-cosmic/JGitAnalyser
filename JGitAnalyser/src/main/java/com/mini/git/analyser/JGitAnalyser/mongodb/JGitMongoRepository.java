package com.mini.git.analyser.JGitAnalyser.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface JGitMongoRepository extends MongoRepository<RepoDocument, String> {
}
