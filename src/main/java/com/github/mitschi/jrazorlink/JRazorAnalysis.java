package com.github.mitschi.jrazorlink;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import at.ac.uniklu.edu.arama.model.SDG;
import at.ac.uniklu.edu.arama.model.parser.exceptions.OperationCanceledException;
import at.ac.uniklu.edu.mkesse.slicemetrics.SliceMetricsRunner;

public class JRazorAnalysis {
	
	private String separator = ";";
	
	public JRazorAnalysis() {
		
	}
	
	private Map<String, Double> aggregateMetrics(String jrazorMetrics){
		
		double tightness = 0d, coverage = 0d, projectCoupling = 0d, calledClasses = 0d; //, fanout = 0d, overlap = 0d;
		
		String[] lines = jrazorMetrics.split("\r\n");
		for(int i = 1; i < lines.length; i++) {
			String[] lineParts = lines[i].split(separator);
			
			tightness += Double.parseDouble(lineParts[1]);
			coverage += Double.parseDouble(lineParts[4]);
			// overlap += Double.parseDouble(lineParts[5]);
			projectCoupling += (Double.parseDouble(lineParts[6]) + Double.parseDouble(lineParts[8])); // 10, distinct classes
			// fanout += (Double.parseDouble(lineParts[7]) + Double.parseDouble(lineParts[9])); // 11, distinct classes
			calledClasses += Double.parseDouble(lineParts[10]);
			
		}
		
		int numClasses = (lines.length > 1 ? lines.length-1 : 1);
		Map<String, Double> aggregatedMetrics = new HashMap<>();
		aggregatedMetrics.put("slice-tightness", tightness / numClasses);
		aggregatedMetrics.put("slice-coverage", coverage / numClasses);
		aggregatedMetrics.put("project-coupling", projectCoupling / numClasses);
		aggregatedMetrics.put("called-classes", calledClasses / numClasses);
		
		return aggregatedMetrics;
	}
	
	/**
	 * Aggregates JRazor coupling and cohesion metrics
	 * 
	 * @return
	 */
	public Map<String, Double> getAggregatedMetrics(String classDir) {
		
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try(PrintStream out = new PrintStream(baos, true, "UTF-8")){
			boolean takeUnknownMethods = false;
			boolean methodsFromSDG = false;
			boolean sizeNodes = false;
			boolean pdgSlice = true;
			boolean localAssignments = true;
			boolean couplingFields = true;
			boolean poolMethod = false;
			boolean simple = true;
			SliceMetricsRunner.getSliceMetrics(classDir, new String[]{""}, 
					out, takeUnknownMethods, methodsFromSDG, sizeNodes, pdgSlice, localAssignments, 
					couplingFields, separator, poolMethod, simple);
		} catch (Exception e) {
			//probably, binary not available or directory is not found
			e.printStackTrace();
		}
		
		String data = new String(baos.toByteArray(), StandardCharsets.UTF_8);
		return aggregateMetrics(data);
	}
	
	public SDG getSDG(String classDir) {
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try(PrintStream out = new PrintStream(baos, true, "UTF-8")){
			boolean takeUnknownMethods = false;
			boolean methodsFromSDG = false;
			boolean sizeNodes = false;
			boolean pdgSlice = true;
			boolean localAssignments = true;
			boolean couplingFields = true;
			boolean poolMethod = false;
			boolean simple = true;
			return SliceMetricsRunner.getSliceMetrics(classDir, new String[]{""}, 
					out, takeUnknownMethods, methodsFromSDG, sizeNodes, pdgSlice, localAssignments, 
					couplingFields, separator, poolMethod, simple);
		} catch (Exception e) {
			//probably, binary not available or directory is not found
			e.printStackTrace();
		}
		
		return null;
	}

}
