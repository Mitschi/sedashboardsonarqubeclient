package com.github.mitschi.sonarlink;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Class which represents the overview of the sonar metrics analysis.
 * @author kema
 */
public class SonarDataSet{
	
	/** Root node of the sonar analysis. */
	private SonarProject root;
	
	/** HashMap of metric ids to the metrics names. */
	private Map<Integer, String> metricIds;
	
	/** Flag whether the set is already populated. Initially false. */
	private boolean populated = false;
	
	/**
	 * Constructor, setting the root node and the map of metric ids-names.
	 * @param root		root node of analysis
	 * @param metricIds	map of metrics ids-names
	 */
	public SonarDataSet(SonarProject root, Map<Integer, String> metricIds){
		this.root = root;
		this.metricIds = metricIds;
	}
	
	/**
	 * Returns the root project.
	 */
	public SonarProject getRootProject(){
		return this.root;
	}

	/**
	 * Returns the root project id.
	 */
	public String getRootId(){
		return root.getId();
	}
	
	/**
	 * Returns the map of metrics ids-names.
	 */
	public Map<Integer, String> getMetricIds(){
		return this.metricIds;
	}
	
	/**
	 * Constructs a map of metric names to sonar metric objects.
	 * @param metrics	the map of metric ids to sonar metric objects
	 * @return			the constructed map of metrics ids-names
	 */
	public Map<String, Double> constructMetrics(Map<Integer, Double> metrics){
		Map<String, Double> construct = new HashMap<String, Double>();
		
		Set<Integer> keys = metrics.keySet();
		
		for(int i : keys){
			String s = this.metricIds.get(i);
			construct.put(s, metrics.get(i));
		}
		
		return construct;
	}
	
	/**
	 * Tries to retrieve an id for the parameter name of a metric.
	 * @param name	name of the metric
	 * @return		the id of the metric if found, -1 otherwise
	 */
	public int getMetricId(String name){
		Set<Integer> keys = this.metricIds.keySet();
		
		for(int i : keys){
			if(name.equals(this.metricIds.get(i))){
				return i;
			}
		}
		
		return -1;
	}
	
	/**
	 * @return the populated
	 */
	public boolean isPopulated() {
		return populated;
	}

	/**
	 * @param populated the populated to set
	 */
	public void setPopulated(boolean populated) {
		this.populated = populated;
	}
	
	/** 
	 * Constructs a map of name-metric from the root map of id-metric.
	 * 
	 * @return	map of metric name to metric
	 */
	public Map<String, Double> getRootMetrics() {
		SonarProject sp = this.getRootProject();
		return this.constructMetrics(sp.getMetrics());
	}
	
	/**
	 * Prints the export header, and then starts recursive printing through the method exportData.
	 * @param out		PrintStream for output
	 * @param separator	separator for .csv output
	 */
	public void exportRoot(PrintStream out, String separator){
		out.println("id" + separator + "rootId"
				//+ separator + "snapshot"
				+ separator + "name" + separator + "longName"
				+ separator + "scope" + separator + "metrics");
		this.exportData(out, separator, this.root);
	}
	
	/**
	 * Exports data from called node, and exports all child nodes.
	 * @param out		PrintStream for output
	 * @param separator	separator for . csv output
	 * @param data		SonarProject data which shall be exported
	 */
	private void exportData(PrintStream out, String separator, SonarProject data){
		out.println(data.toString(separator, this.metricIds));
		
		for(SonarProject s : data.getChildModules()){
			this.exportData(out, separator, s);
		}
	}
	
	@Override
	public String toString(){
		if(this.root != null){
			return this.root.toString();
		}
		return super.toString();
	}
}