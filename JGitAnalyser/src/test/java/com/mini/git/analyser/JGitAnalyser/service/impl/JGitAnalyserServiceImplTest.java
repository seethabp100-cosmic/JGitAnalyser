package com.mini.git.analyser.JGitAnalyser.service.impl;

import com.mini.git.analyser.JGitAnalyser.mongodb.CommitData;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Constants;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Method;
import java.time.Instant;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class JGitAnalyserServiceImplTest {

    @Test
    void toResponse_returnsNullForNullCommit() throws Exception {
        JGitAnalyserServiceImpl svc = new JGitAnalyserServiceImpl();
        Method m = JGitAnalyserServiceImpl.class.getDeclaredMethod("toResponse", CommitData.class);
        m.setAccessible(true);
        Object res = m.invoke(svc, new Object[]{null});
        assertNull(res);
    }

    @Test
    void toResponse_translatesCommitData() throws Exception {
        JGitAnalyserServiceImpl svc = new JGitAnalyserServiceImpl();
        Method m = JGitAnalyserServiceImpl.class.getDeclaredMethod("toResponse", CommitData.class);
        m.setAccessible(true);

        CommitData cd = CommitData.builder()
                .commitHash("abcdef123456")
                .shortHash("abcdef1")
                .message("msg")
                .authorName("name")
                .authorEmail("e@x.com")
                .analysedAt(Instant.now())
                .build();

        Object res = m.invoke(svc, cd);
        assertNotNull(res);
        // rely on record's toString for basic sanity
        assertTrue(res.toString().contains("abcdef123456"));
    }

    @Test
    void checkoutToBranch_throwsWhenBranchMissing() throws Exception {
        JGitAnalyserServiceImpl svc = new JGitAnalyserServiceImpl();
        Method m = JGitAnalyserServiceImpl.class.getDeclaredMethod("checkoutToBranch", Git.class, String.class);
        m.setAccessible(true);

        Git git = Mockito.mock(Git.class);
        ListBranchCommand listCmd = Mockito.mock(ListBranchCommand.class);
        when(git.branchList()).thenReturn(listCmd);
        when(listCmd.setListMode(Mockito.any())).thenReturn(listCmd);
        when(listCmd.call()).thenReturn(Collections.emptyList());

        Exception ex = assertThrows(Exception.class, () -> m.invoke(svc, git, "missing-branch"));
        // InvocationTargetException wraps the actual exception
        assertNotNull(ex);
    }
}
