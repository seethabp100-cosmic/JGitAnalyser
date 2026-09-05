package com.mini.git.analyser.JGitAnalyser.mongodb.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Document(collection = "git_file_changes")
@CompoundIndex(
        name = "commit_file_idx",
        def = "{'commitId': 1, 'filePath': 1}"
)
public class FileChangeDocument {
    @Id
    private String fileChangeId;

    private String analysisId;
    private String commitId;

    private String filePath;
    private String changeType;

    private int additions;
    private int deletions;
    private String codeChange;
}
