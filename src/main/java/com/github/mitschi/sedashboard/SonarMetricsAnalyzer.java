package com.github.mitschi.sedashboard;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sonarsource.scanner.api.EmbeddedScanner;
import org.sonarsource.scanner.api.StdOutLogOutput;
import org.sonarsource.scanner.cli.ScannerVersion;

import com.github.mitschi.jrazorlink.JRazorAnalysis;

import com.github.mitschi.sonarlink.SonarDataSet;
import com.github.mitschi.sonarlink.SonarLink;

public class SonarMetricsAnalyzer {
	
	private static List<String> includeMetrics = Arrays.asList("ncloc", "comment_lines",
			"complexity",
			"coverage",
			"duplicated_lines",
			"violations", "blocker_violations", "critical_violations", "major_violations", "minor_violations", "info_violations",
			"code_smells", "bugs", "vulnerabilities",
			"sqale_index");
	
    /**
     *
     * @param projectSrcRoot File to the root of the source files to be analyzed
     * @param projectBinaryRoot File to the root of the binary files to be analyzed. If no binaries could be produced, this variable is null.
     * @param sonarConnectionDetails The details to connect to the Sonar
     * @return An object that contains all the metrics and analysis results of the respective revision of the project.
     */
    public static SonarCommitAnalysis analyzeCommit(String commitHash, File projectSrcRoot, File projectBinaryRoot, SonarConnectionDetails sonarConnectionDetails) {
    	
    	// start sonar runner analysis in project src root
    	EmbeddedScanner es = EmbeddedScanner.create("ScannerCli", ScannerVersion.version(), new StdOutLogOutput());

    	if(projectBinaryRoot == null) {
    		// create build if not exists (needed anyway for sonar analysis)
    		File build = new File(projectSrcRoot + File.separator + "build");
    		System.out.println("dir " + build.getAbsolutePath() + " created: " + build.mkdir());
    	}
    	
    	String projectName = projectSrcRoot.getName();
    	Map<String, String> analysisProperties = new HashMap<>();
    	
    	analysisProperties.put("sonar.host.url", sonarConnectionDetails.getSonarURL());
    	analysisProperties.put("sonar.projectBaseDir", projectSrcRoot.getAbsolutePath());
    	analysisProperties.put("sonar.projectKey", projectName);
    	analysisProperties.put("sonar.projectName", projectName);

    	analysisProperties.put("sonar.sources", ".");
    	analysisProperties.put("sonar.java.binaries", "build");
    	analysisProperties.put("sonar.projectVersion", commitHash);

    	
    	es.addGlobalProperties(analysisProperties);
    	System.out.println(es.globalProperties());
    	
    	es.start();
    	es.execute(analysisProperties);
    	
    	// wait for sonarqube results to be fetchable
    	try {
    		System.out.println("wait for sonarqube");
			Thread.sleep(10_000);
		} catch (InterruptedException e) {
			System.err.println("wait for sonarqube not successful");
			e.printStackTrace();
		}
    	
		// get sonar metrics
		SonarLink s = new SonarLink(sonarConnectionDetails.getDatabaseConnection(), 
				sonarConnectionDetails.getUsername(), sonarConnectionDetails.getPassword(), includeMetrics);
		SonarDataSet dataSet = s.fetchProjectMetrics(projectName);
		
		SonarCommitAnalysis sonarCommitAnalysis = new SonarCommitAnalysis();
		Map<String, Double> sonarMetrics = dataSet.getRootMetrics();
		sonarCommitAnalysis.setSonarMetrics(sonarMetrics);
		
		if(projectBinaryRoot == null) {
			sonarCommitAnalysis.setCouplingAndCohesionMetrics(new HashMap<>());
		} else {
			// TODO: build src with eclipse jdt compiler
			// flags: -g -preserveAllLocals
			
			// start jrazor analysis
			/*
			JRazorAnalysis jra = new JRazorAnalysis();
			Map<String, Double> cchMetrics = jra.getAggregatedMetrics(projectBinaryRoot.getAbsolutePath());
			*/
			
			sonarCommitAnalysis.setCouplingAndCohesionMetrics(new HashMap<>());
		}
    	
        return sonarCommitAnalysis;
    }

}
