package com.mini.git.analyser.JGitAnalyser.dto.response;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class FileChangeResponse{
    String changeType;
    String filePath;

    int additions;
    int deletions;
    String codeChange;
}

