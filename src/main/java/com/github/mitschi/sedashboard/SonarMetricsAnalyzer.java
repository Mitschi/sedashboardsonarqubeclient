package com.github.mitschi.sedashboard;

import java.io.File;

public class SonarMetricsAnalyzer {
    /**
     *
     * @param projectSrcRoot File to the root of the source files to be analyzed
     * @param projectBinaryRoot File to the root of the binary files to be analyzed. If no binaries could be produced, this variable is null.
     * @param sonarConnectionDetails The details to connect to the Sonar
     * @return An object that contains all the metrics and analysis results of the respective revision of the project.
     */
    public SonarCommitAnalysis analyzeCommit(File projectSrcRoot, File projectBinaryRoot, SonarConnectionDetails sonarConnectionDetails) {
        return null;
    }

}
