package com.mini.git.analyser.JGitAnalyser.service.impl;

import com.mini.git.analyser.JGitAnalyser.dto.request.AIFileRequest;
import com.mini.git.analyser.JGitAnalyser.dto.response.CommitChangeResponse;
import com.mini.git.analyser.JGitAnalyser.dto.response.CommitResponse;
import com.mini.git.analyser.JGitAnalyser.dto.response.FileChangeResponse;
import com.mini.git.analyser.JGitAnalyser.service.DiffAnalysisService;
import com.mini.git.analyser.JGitAnalyser.service.util.JGitAnalyzeServiceUtil;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.patch.FileHeader;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.EmptyTreeIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class DiffAnalysisServiceImple implements DiffAnalysisService {
    private static final Logger logger = LoggerFactory.getLogger(DiffAnalysisServiceImple.class);

    @Autowired
    JGitAnalyzeServiceUtil jGitAnalyzeServiceUtil;

    @Override
    public CommitResponse analyzeCommit(Repository repository, RevCommit commit) throws IOException {
        logger.info("[Service] Diff data Analysis, Start");
        if (commit.getParentCount() == 0) {
            logger.warn("No Parent found in commit: {}", commit);
            return analyzeInitialCommit(repository, commit);
        }

        RevCommit parentCommit = commit.getParent(0);
        try (RevWalk revWalk = new RevWalk(repository)) {
            RevCommit parsedParent = revWalk.parseCommit(parentCommit.getId());

            RevCommit parsedCommit = revWalk.parseCommit(commit.getId());

            return calculateDiffs(repository, parsedParent, parsedCommit);
        }
    }

    private CommitResponse calculateDiffs(Repository repository, RevCommit parsedParent, RevCommit parsedCommit) throws IOException {
        logger.info("[Service] Diff Calculation Start");

        List<FileChangeResponse> listOfFileChangeResponse = new ArrayList<>();

        int total_additions = 0;
        int total_deletions = 0;
        try (DiffFormatter formatter = new DiffFormatter(new ByteArrayOutputStream())) {

            formatter.setRepository(repository);
            formatter.setDetectRenames(true);

            List<DiffEntry> diffEntries = formatter.scan(parsedParent.getTree(), parsedCommit.getTree());
            for (DiffEntry entries : diffEntries) {
                FileChangeResponse response = analyzeFileChanges(formatter, entries, repository);
                listOfFileChangeResponse.add(response);
                total_additions = total_additions + response.getAdditions();
                total_deletions = total_deletions + response.getDeletions();
            }
        }

        //List<AIFileRequest> aiFiles = listOfFileChangeResponse.stream().map(JGitAnalyzeServiceUtil::toAIFile).toList();

        return new CommitResponse(
                parsedCommit.getName().substring(0,7),
                parsedCommit.getName(),
                parsedCommit.getName().substring(0,7),
                parsedCommit.getAuthorIdent().getWhen().toInstant(),
                parsedCommit.getFullMessage(),
                parsedCommit.getAuthorIdent().getName(),
                parsedCommit.getAuthorIdent().getEmailAddress(),
                parsedParent.getName(),
                listOfFileChangeResponse.size(),
                total_additions,
                total_deletions,
                listOfFileChangeResponse,
                null);
    }

    private FileChangeResponse analyzeFileChanges(DiffFormatter formatter, DiffEntry entries, Repository repository) throws IOException {

        String codeChange = "";
        int additions = 0;
        int deletions = 0;

        FileHeader fileHeader = formatter.toFileHeader(entries);

        EditList editList = fileHeader.toEditList();

        for (Edit edit : editList) {
            additions = additions + edit.getEndB() - edit.getBeginB();
            deletions = deletions + edit.getEndA() - edit.getBeginA();
        }

        if (fileHeader.getPatchType() == FileHeader.PatchType.UNIFIED) {
            codeChange = getPatchContent(repository, entries);
        }
        return new FileChangeResponse(
                getChangeType(entries),
                getPath(entries.getNewPath()),
                additions,
                deletions,
                codeChange
        );
    }

    private String getChangeType(DiffEntry entries) {
        return switch (entries.getChangeType()){
            case ADD -> "ADD";
            case MODIFY -> "MODIFY";
            case DELETE -> "DELETE";
            case COPY -> "COPY";
            case RENAME -> "RENAME";
        };
    }

    private String getPatchContent(Repository repository, DiffEntry entries) {
        logger.info("[Service] getPatchContent");
        try{
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            try(DiffFormatter diffFormatter = new DiffFormatter(outputStream)){
                diffFormatter.setRepository(repository);
                diffFormatter.setDetectRenames(true);
                diffFormatter.format(entries);
            }
            return outputStream.toString(StandardCharsets.UTF_8);
        } catch (Exception e) {
            logger.error("Error, Unable to generate Patch for Path::{}", entries.getNewPath());
            logger.error("Error, Failed to Get Patch :: {}", e.getMessage());
        }
        return " ";
    }

    private String getPath(String path) {
        if(DiffEntry.DEV_NULL.equals(path)){
            return  null;
        }
        return path;
    }

    private CommitResponse analyzeInitialCommit(
            Repository repository,
            RevCommit commit) {

        logger.info("[Service] Analyze Initial Commit");

        try {
            List<FileChangeResponse> fileChanges = new ArrayList<>();
            int additions = 0;

            try (DiffFormatter formatter =
                         new DiffFormatter(new ByteArrayOutputStream())) {

                formatter.setRepository(repository);

                AbstractTreeIterator oldTreeItr = new EmptyTreeIterator();

                CanonicalTreeParser newTreeParser = new CanonicalTreeParser();

                try (ObjectReader reader = repository.newObjectReader()) {
                    newTreeParser.reset(reader, commit.getTree());
                }

                List<DiffEntry> diffEntryList =
                        formatter.scan(oldTreeItr, newTreeParser);

                for (DiffEntry entry : diffEntryList) {

                    EditList edits =
                            formatter.toFileHeader(entry).toEditList();

                    int fileAdditions = 0;

                    for (var edit : edits) {
                        fileAdditions +=
                                edit.getEndB() - edit.getBeginB();
                    }

                    additions += fileAdditions;

                    fileChanges.add(
                            new FileChangeResponse(
                                    "ADD",
                                    entry.getNewPath(),
                                    fileAdditions,
                                    0,
                                    null
                            )
                    );
                }
            }

            List<AIFileRequest> aiFiles = fileChanges.stream()
                    .map(jGitAnalyzeServiceUtil::toAIFile)
                    .toList();

            return new CommitResponse(
                    commit.getName().substring(0,7),
                    commit.getName(),
                    commit.getName().substring(0,7),
                    commit.getAuthorIdent().getWhen().toInstant(),
                    commit.getFullMessage(),
                    commit.getAuthorIdent().getName(),
                    commit.getAuthorIdent().getEmailAddress(),
                    commit.getName(),
                    fileChanges.size(),
                    additions,
                    0,
                    fileChanges,
                    null // AI analysis will be populated later
            );

        } catch (Exception e) {
            logger.error(
                    "Error, Failed to Analyze the Initial Commit: {}",
                    e.getMessage(),
                    e
            );
        }

        return null;
    }
}
