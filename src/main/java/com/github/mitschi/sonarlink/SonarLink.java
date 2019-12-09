package com.github.mitschi.sonarlink;

import java.io.PrintStream;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class offering a link to the sonar data base, which is capable of extracting metrics information,
 * reporting, exporting and importing them. 
 * @author makessel
 */
public class SonarLink {
	
	/** Db connection string. */
	private String dbConnectionString;
	
	/** User name for accessing the db". */
	private String user;
	
	/** Password for accessing the db, with default value "sonar". */
	private String password;
	
	/** List of metric ids /names that shall be included in analysis. */
	private List<Integer> includeIds = new ArrayList<>();
	private List<String> includeMetrics;
		
	/**
	 * Constructor setting the URL, user and password of the database.
	 * @param dbString	db connection string
	 * @param sonarUrl	URL to the database
	 * @param user		username
	 * @param password	password
	 * @param includeIds	ids of metrics to be included in analysis
	 */
	public SonarLink(String dbString, String user, String password, List<String> includeMetrics){
		this.dbConnectionString = dbString;
		this.user = user;
		this.password = password;
		this.includeMetrics = includeMetrics;
	}
			
	/**
	 * Fetches and returns the connection to the database.
	 * @return
	 */
	private Connection getDatabaseConnection(){
		Connection con = null;
		try{
			con = DriverManager.getConnection(this.dbConnectionString, this.user, this.password);
		} catch(SQLException s){
			System.out.println("error connecting to the database: " + s.getMessage());
			System.out.println("url: " + this.dbConnectionString + ", user: " + user + ", password:" + password);
		}
		
		return con;
	}
	
	/**
	 * Fetches the list of metrics from the database, and stores them in id-name pairs.
	 *  
	 * @return	map of id-name pairs of sonar metrics
	 */
	public Map<Integer, String> getSonarMetrics(){
		Map<Integer, String> m = new HashMap<Integer, String>();
		String query = "select id, name from metrics;";
		
		try(Connection con = this.getDatabaseConnection();
				Statement st = con.createStatement();
				ResultSet rs = st.executeQuery(query)){
			while(rs.next()){
				int id = rs.getInt(1);
				String name = rs.getString(2);
				if(this.includeMetrics.contains(name)) {
					m.put(id, name);
					this.includeIds.add(id);
				}
			}
		} catch(SQLException s){
			System.out.println("error fetching sonar metrics: " + s.getMessage());
		}
		
		return m;
	}
		
	/**
	 * Fetches a root project with parameter project name from the database. 
	 * @param projectName	the name of the project to be fetched
	 * @return				the SonarProject with fetched information
	 */
	public SonarProject getProject(String projectName){
		String query = "select uuid, long_name, scope, root_uuid from projects "
				+ "where name like '" + projectName + "' "
				+ "and scope like 'PRJ';";
		
		SonarProject sonar = null;
		
		try(Connection con = this.getDatabaseConnection();
				Statement st = con.createStatement();
				ResultSet rs = st.executeQuery(query)){
			while(rs.next()){
				//in order: name, long name, uuid, scope, root_uuid			
				sonar = new SonarProject(projectName, rs.getString(2), rs.getString(1), rs.getString(3), rs.getString(4));
			}
		} catch(SQLException s){
			System.out.println("error fetching project: " + s.getMessage());
		}
		
		return sonar;
	}
	
	/**
	 * Populates the root project with all corresponding child modules.
	 * @param root	root project for which the hierarchy shall be filled
	 */
	public void populateProject(SonarProject root){
		//fetch the projects that have root_id from given project
		//two possibilities here: subproject that have their own id and childmodules -> recursive call
		//and normal projects (dirs and files) that do not have childmodules -> just retrieve them.
		String query = "select uuid, name, long_name, scope from projects "
				+ "where root_uuid like '" + root.getId() + "';";
		
		try(Connection con = this.getDatabaseConnection();
				Statement st = con.createStatement();
				ResultSet rs = st.executeQuery(query)){
			while(rs.next()){
				String id = rs.getString(1);
				String name = rs.getString(2);
				String longName = rs.getString(3);
				String scope = rs.getString(4);
				if(root.getProject(id) == null){
					//only add child project if not yet added (important for re-populating)
					if(scope.equals("PRJ")){
						//subproject with childmodules, so create object and call rec. with it
						SonarProject child = new SonarProject(name, longName, id, scope, root.getId());
						this.populateProject(child);
						root.addChildModule(child);
					} else{
						//normal project, will just be added to childmodules of root
						SonarProject child = new SonarProject(name, longName, id, scope, root.getId());
						root.addChildModule(child);
					}
				}
			}
		} catch(SQLException s){
			System.out.println("error fetching project: " + s.getMessage());
		}
	}
	
	public void getLastAnalysis(SonarProject root){
		String query = "select uuid from snapshots "
				+ "where component_uuid like '" + root.getId() + "' and islast = true;";
		
		try(Connection con = this.getDatabaseConnection();
				Statement st = con.createStatement();
				ResultSet rs = st.executeQuery(query)){
			while(rs.next()){
				//with project_id, select the module where the snapshot belongs to
				String analysisUUID = rs.getString(1);
				root.setAnalysisUUID(analysisUUID);
			}
		} catch(SQLException s){
			System.out.println("error fetching last analysis: " + s.getMessage());
		}
	}
	
	/**
	 * Fetches the metrics for the root project, and assigns them to modules.
	 * @param root
	 */
	public void getMetrics(SonarProject root){
		String query = "select value, metric_id, text_value, variation_value_1 "
				+ "from project_measures "
				+ "where analysis_uuid like '" + root.getAnalysisUUID() + "' order by metric_id;";
		
		try(Connection con = this.getDatabaseConnection();
				Statement st = con.createStatement();
				ResultSet rs = st.executeQuery(query)){
			while(rs.next()){
				double value = rs.getDouble(1);
				int metric = rs.getInt(2);
				//text value (3) not considered now
				double varValue = rs.getDouble(4);
				
				if(this.includeIds.contains(metric)){
					double m = (value == 0 && varValue != 0 ? varValue : value);
					root.putMetric(metric, m);
				}
			}
		} catch(SQLException s){
			System.out.println("error fetching metrics: " + s.getMessage());
		}
		
		for(SonarProject s : root.getChildModules()){
			//recursively fetch data from all child projects
			this.getMetrics(s);
		}
	}
	
	/**
	 * Fetches a sonar data set for the parameter project name.
	 * Fetches all metrics in the includeIds set.
	 * 
	 * @param projectName	project name of the project to be fetched
	 * @return				SonarDataSet to be saved
	 */
	public SonarDataSet fetchProjectMetrics(String projectName) {
		SonarDataSet dataSet = new SonarDataSet(this.getProject(projectName), this.getSonarMetrics());
		
		SonarProject root = dataSet.getRootProject();
		if(root != null) {
			//this.populateProject(root);
			this.getLastAnalysis(root);
			this.getMetrics(root);
		}
		
		return dataSet;
	}
	
	/**
	 * Exports the information of the parameter data set.
	 * @param out		PrintStream for output
	 * @param separator	separator for .csv output
	 * @param set		the data set for exporting
	 */
	public void exportDataSet(PrintStream out, String separator, SonarDataSet set){
		set.exportRoot(out, separator);
	}
			
}