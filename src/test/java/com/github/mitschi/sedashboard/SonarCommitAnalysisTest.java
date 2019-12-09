package com.github.mitschi.sedashboard;

import java.io.File;

import org.junit.Ignore;
import org.junit.Test;

/**
 * Unit test for SonarCommitAnalysis.
 */
public class SonarCommitAnalysisTest
{
    /**
     * Starts sonar metric analysis (start sonar scanner, get metrics from sonar db)
     * and asserts that there are metrics extracted.
     */
    @Test
	@Ignore
    public void SonarMetricAnalyseCommit()
    {
    	File projectSrcRoot = new File("D:\\git\\KAUA\\kaua-server");
    	File projectBinaryRoot = new File("TODO");
    	
    	// TODO: add suitable db driver dependency!
    	SonarConnectionDetails sonarConnectionDetails = new SonarConnectionDetails("jdbc:h2:tcp://localhost:9092/sonar", 
    			"" /* user */,
    			"" /* pwd */,
				"http://localhost:9000" /* url */);
    	
    	SonarCommitAnalysis sca = SonarMetricsAnalyzer.analyzeCommit("",projectSrcRoot, projectBinaryRoot, sonarConnectionDetails);
    	System.out.println(sca.getSonarMetrics());
    	assert(sca.getSonarMetrics() != null);
    	assert(sca.getSonarMetrics().size() == 15);
    }
}
