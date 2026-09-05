package com.mini.git.analyser.JGitAnalyser.controller;

import com.mini.git.analyser.JGitAnalyser.dto.request.JGitRequest;
import com.mini.git.analyser.JGitAnalyser.dto.response.JGitResponse;
import com.mini.git.analyser.JGitAnalyser.service.JGitAnalyserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class JGitAnalysisControllerTest {

    @Test
    void analyseGitRepo_returnsResponseFromService() {
        JGitAnalyserService svc = Mockito.mock(JGitAnalyserService.class);
        JGitAnalysisController ctrl = new JGitAnalysisController();
        // inject mock
        try {
            var f = JGitAnalysisController.class.getDeclaredField("jgitAnalyserService");
            f.setAccessible(true);
            f.set(ctrl, svc);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        JGitRequest req = new JGitRequest("https://x/y.git", "main", true);
        JGitResponse resp = new JGitResponse("repo","main",0,0,0,0,List.of());
        when(svc.analyze(req.gitUrl(), req.branch(), true)).thenReturn(resp);

        ResponseEntity<JGitResponse> r = ctrl.analyseGitRepo(req);
        assertEquals(200, r.getStatusCodeValue());
        assertEquals(resp, r.getBody());
    }
}
