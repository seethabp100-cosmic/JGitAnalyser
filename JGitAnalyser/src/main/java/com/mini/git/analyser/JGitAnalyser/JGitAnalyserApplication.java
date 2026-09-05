package com.mini.git.analyser.JGitAnalyser;

import com.mini.git.analyser.JGitAnalyser.properties.GeminiProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(GeminiProperties.class)
public class JGitAnalyserApplication {

    public static void main(String[] args) {
        SpringApplication.run(JGitAnalyserApplication.class, args);
    }

}
