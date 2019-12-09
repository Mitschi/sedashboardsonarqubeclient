package com.github.mitschi.sonarlink;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract super class representing common information from sonar data objects.
 * @author kema
 */
public abstract class SonarData {

	/** Name of the data object. */
	private String name;
	
	/** The long (full db) name of the data object. */
	private String longName;
	
	/** The root id of this object. */
	private String rootId;
	
	/** The own id of this object. */
	private String id;
	
	/** The scope String of this object (FIL, DIR or PRJ). */
	private String scope;
	
	/** Analysis UUID for metrics extraction. */
	private String analysisUUID;
	
	/** List of child modules of the current sonar data. */
	private List<SonarProject> childModules;
	
	/**
	 * Constructor of sonar data objects, setting relevant basic information.
	 * @param name		the name
	 * @param longName	the long db name
	 * @param id		the id
	 * @param scope		the scope
	 * @param rootId	the id of the root object for this object
	 */
	public SonarData(String name, String longName, String id, String scope, String rootId){
		this.name = name;
		this.longName = longName;
		this.id = id;
		this.rootId = rootId;
		this.scope = scope;
		this.analysisUUID = "";
		this.childModules = new ArrayList<SonarProject>();
	}
	
	/**
	 * Returns the name.
	 */
	public String getName(){
		return this.name;
	}
	
	/**
	 * Returns the own id.
	 */
	public String getId(){
		return this.id;
	}
	
	/**
	 * Returns the id of the root object.
	 */
	public String getRootId(){
		return this.rootId;
	}
	
	/**
	 * Returns the long name.
	 */
	public String getLongName(){
		return this.longName;
	}
	
	/**
	 * Sets the long name.
	 */
	public void setLongName(String longName){
		this.longName = longName;
	}
	
	/**
	 * Returns the scope.
	 */
	public String getScope(){
		return this.scope;
	}
	
	/**
	 * Sets the scope.
	 */
	public void setScope(String scope){
		this.scope = scope;
	}
	
	public String getAnalysisUUID() {
		return this.analysisUUID;
	}
	
	public void setAnalysisUUID(String analysisUUID) {
		this.analysisUUID = analysisUUID;
	}
	
	/**
	 * Returns the list of child modules.
	 */
	public List<SonarProject> getChildModules(){
		return this.childModules;
	}
	
	/**
	 * Adds the parameter project to the child modules.
	 */
	public void addChildModule(SonarProject sonar){
		this.childModules.add(sonar);
	}
		
	/**
	 * Returns a string representation of sonar data objects.
	 * @param separator	the separator for output
	 */
	public String toString(String separator){
		return id 
				+ separator + rootId 
				//+ separator + this.getSnapshot() 
				+ separator + name 
				+ separator + longName 
				+ separator + scope;
	}
	
	@Override
	public String toString(){
		return this.name + ";" + this.scope;
	}
}