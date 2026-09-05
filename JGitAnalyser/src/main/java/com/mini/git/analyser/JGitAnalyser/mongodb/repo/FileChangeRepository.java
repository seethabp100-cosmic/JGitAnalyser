package com.mini.git.analyser.JGitAnalyser.mongodb.repo;

import com.mini.git.analyser.JGitAnalyser.mongodb.document.FileChangeDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileChangeRepository extends MongoRepository<FileChangeDocument, String> {

}
