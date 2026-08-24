package com.mini.git.analyser.JGitAnalyser.util;

import com.mini.git.analyser.JGitAnalyser.service.util.JGitAnalyzeServiceUtil;
import org.eclipse.jgit.lib.Repository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class JGitAnalyzeServiceUtilTest {

    private final JGitAnalyzeServiceUtil util = new JGitAnalyzeServiceUtil();

    @Test
    void extractRepoOwnerName_removesGitSuffix() {
        String url = "https://github.com/user/repo.git";
        String owner = util.extractRepoOwnerName(url);
        assertEquals("/repo", owner);

        String url2 = "https://github.com/user/repo";
        assertEquals("/repo", util.extractRepoOwnerName(url2));
    }

    @Test
    void extractRepoName_returnsParentSegment() {
        String url = "https://github.com/user/repo.git";
        assertEquals("user", util.extractRepoName(url));

        String url2 = "https://example.com/a/b/c.git";
        assertEquals("b", util.extractRepoName(url2));
    }

    @Test
    void deleteTempDir_deletesFilesAndDirectories() throws Exception {
        Path tmp = Files.createTempDirectory("jgit-test-");
        Path sub = tmp.resolve("sub");
        Files.createDirectory(sub);
        Files.createFile(sub.resolve("f.txt"));

        assertTrue(Files.exists(tmp));
        util.deleteTempDir(tmp);
        assertFalse(Files.exists(tmp));
    }

    @Test
    void extractCommitDetails_branchNotFound_throws() throws Exception {
        Repository repo = Mockito.mock(Repository.class);
        when(repo.resolve("nonexistent")).thenReturn(null);
        when(repo.resolve("/refs/remotes/origin/nonexistent")).thenReturn(null);

        Exception ex = assertThrows(IllegalArgumentException.class, () -> util.extractCommitDetails(repo, "nonexistent"));
        assertTrue(ex.getMessage().contains("Unable to Resolve the branch"));
    }
}
