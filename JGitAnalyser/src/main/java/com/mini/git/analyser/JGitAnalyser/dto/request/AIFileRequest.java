package com.mini.git.analyser.JGitAnalyser.dto.request;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AIFileRequest {
    private String filePath;
    private String changeType;

    private int additions;
    private int deletions;

    private String codeChange;
}
