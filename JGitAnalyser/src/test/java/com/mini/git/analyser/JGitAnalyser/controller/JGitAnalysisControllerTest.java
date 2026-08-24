package com.mini.git.analyser.JGitAnalyser.controller;

import com.mini.git.analyser.JGitAnalyser.controller.JGitAnalysisController;
import com.mini.git.analyser.JGitAnalyser.dto.JGitRequest;
import com.mini.git.analyser.JGitAnalyser.dto.JGitResponse;
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

        JGitRequest req = new JGitRequest("https://x/y.git", "main");
        JGitResponse resp = new JGitResponse("id","owner","repo","main",0,null,List.of());
        when(svc.analyze(req.gitUrl(), req.branch())).thenReturn(resp);

        ResponseEntity<JGitResponse> r = ctrl.analyseGitRepo(req);
        assertEquals(200, r.getStatusCodeValue());
        assertEquals(resp, r.getBody());
    }
}
