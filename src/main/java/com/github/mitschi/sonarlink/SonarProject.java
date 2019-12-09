package com.github.mitschi.sonarlink;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Class which represents the sonar data for one specific project module, that is the whole project (PRJ),
 * a directory (DIR) or specific source files (FIL). Extends the abstract SonarData class.
 * @author kema
 */
public class SonarProject extends SonarData{
	
	/** Key-Value pair of the metrics retrieved from sonar. The mapping is as follows:
	 * 	First integer for the metrics id,
	 *  second integer for the metrics value for the current sonar project. */
	private Map<Integer, Double> sonarMetrics;
	
	/**
	 * Constructor which sets the basic information through the super constructor,
	 * and initializes the lists.
	 * @param name		name of the module
	 * @param longName	long db name of the module
	 * @param id		the id
	 * @param scope		the scope of the module
	 * @param rootId	the id of the root module of this one
	 */
	public SonarProject(String name, String longName, String id, String scope, String rootId){
		super(name, longName, id, scope, rootId);
		this.sonarMetrics = new HashMap<>();
	}
	
	/**
	 * Puts the key-value pair of id-sonarMetric into the latest HashMap.
	 * @param key	id of the metric
	 * @param value	metric object
	 */
	public void putMetric(int key, double value){ 
		sonarMetrics.put(key, value);
	}
		
	/**
	 * Retrieves the metric to the stored parameter key id from the latest HashMap.
	 * @param key	id of the metric
	 * @return		the associated metric
	 */
	public double retrieveMetric(int key){ 
		return sonarMetrics.get(key);
	}
		
	/**
	 * Returns the whole HashMap of metrics.
	 * @return			HashMap of id-metrics pairs for given version
	 */
	public Map<Integer, Double> getMetrics(){
		return this.sonarMetrics;
	}
	
	/**
	 * Prints the metrics with respect to the parameter descriptor.
	 * @param separator		separator for .csv output
	 * @param descriptor	HashMap of id-metricName pairs
	 * @return				the resulting String
	 */
	public String printMetrics(String separator, Map<Integer, String> descriptor){
		String output = "";
		Set<Integer> keys = sonarMetrics.keySet();
		
		for(int i : keys){
			String metricName = descriptor.get(i);
			if(metricName != null && !metricName.equals("null")){
				output += metricName + separator + this.retrieveMetric(i) + "\n";
			}
		}
		
		return output;
	}
	
	/**
	 * Returns full information about this module.
	 * @param separator		separator for .csv output
	 * @param descriptor	HashMap of id-metricName pairs
	 * @return
	 */
	public String toString(String separator, Map<Integer, String> descriptor){
		String output = super.toString(separator);
		output += separator + this.printMetrics(separator, descriptor);
		return output;
	}
		
	/**
	 * Returns the sonar project that matches the parameter id. If this is not the current project,
	 * it is iterated through the child projects.
	 * @param id	the database id
	 * @return		the matching project, or null
	 */
	public SonarProject getProject(String id){
		if(this.getId() == id){
			return this;
		}
		
		for(SonarProject s : this.getChildModules()){
			SonarProject p = s.getProject(id);
			if(p != null){
				return p;
			}
		}
		
		return null;
	}	
}