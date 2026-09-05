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
@Document(collection = "git_code_changes")
@CompoundIndex(
        name = "file_change_idx",
        def = "{'fileChangeId': 1}"
)
public class CodeChangeDocument {
    @Id
    private String codeChangeId;

    private String analysisId;
    private String commitId;
    private String fileChangeId;

    private String changeType;

    private int oldStartLine;
    private int oldLineCount;

    private int newStartLine;
    private int newLineCount;

    private String code;
}
