package com.mini.git.analyser.JGitAnalyser.controller;

import com.mini.git.analyser.JGitAnalyser.dto.request.JGitRequest;
import com.mini.git.analyser.JGitAnalyser.dto.response.JGitResponse;
import com.mini.git.analyser.JGitAnalyser.service.JGitAnalyserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin("http://localhost:5173")
public class JGitAnalysisController {

    private static final Logger logger = LoggerFactory.getLogger(JGitAnalysisController.class);

    @Autowired
    private JGitAnalyserService jgitAnalyserService;

    /*
     *
     * Request format :
     * {
     *   "gitUrl":"<URI>",
     *   "branch":"<Branch name>"
     * }
     *
     */
    @PostMapping("/analyse")
    public ResponseEntity<JGitResponse> analyseGitRepo(@Valid @RequestBody JGitRequest request) {
        logger.info("Controller: Start, Analysis of Git Repo");
        JGitResponse response = null;
        try {
            response = jgitAnalyserService.analyze(request.gitUrl(), request.branch(), request.includeAIAnalysis());
            logger.info("Controller: response = {}", response);
        } catch (Exception e) {
            logger.error("Error, unable to analyse the gitURL = {}, branch = {}", request.gitUrl(), request.branch());
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok(response);
    }
}
