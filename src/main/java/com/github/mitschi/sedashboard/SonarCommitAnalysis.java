package com.github.mitschi.sedashboard;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

public class SonarCommitAnalysis implements Serializable {
    private Long projectid;
    private String projectName;
    private String revision;

    private Map<String, Double> couplingAndCohesionMetrics;
    private Map<String, Double> sonarMetrics;

    public SonarCommitAnalysis(){}
    public SonarCommitAnalysis(Long projectid, String projectName, String revision, Map<String, Double> couplingAndCohesionMetrics, Map<String, Double> sonarMetrics) {
        this.projectid = projectid;
        this.projectName = projectName;
        this.revision = revision;
        this.couplingAndCohesionMetrics = couplingAndCohesionMetrics;
        this.sonarMetrics = sonarMetrics;
    }

    public Long getProjectid() {
        return projectid;
    }

    public void setProjectid(Long projectid) {
        this.projectid = projectid;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public Map<String, Double> getCouplingAndCohesionMetrics() {
        return couplingAndCohesionMetrics;
    }

    public void setCouplingAndCohesionMetrics(Map<String, Double> couplingAndCohesionMetrics) {
        this.couplingAndCohesionMetrics = couplingAndCohesionMetrics;
    }

    public Map<String, Double> getSonarMetrics() {
        return sonarMetrics;
    }

    public void setSonarMetrics(Map<String, Double> sonarMetrics) {
        this.sonarMetrics = sonarMetrics;
    }

    @Override
    public String toString() {
        return "SonarCommitAnalysis{" +
                "projectid=" + projectid +
                ", projectName='" + projectName + '\'' +
                ", revision='" + revision + '\'' +
                ", couplingAndCohesionMetrics=" + couplingAndCohesionMetrics +
                ", sonarMetrics=" + sonarMetrics +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SonarCommitAnalysis that = (SonarCommitAnalysis) o;
        return Objects.equals(projectid, that.projectid) &&
                Objects.equals(projectName, that.projectName) &&
                Objects.equals(revision, that.revision) &&
                Objects.equals(couplingAndCohesionMetrics, that.couplingAndCohesionMetrics) &&
                Objects.equals(sonarMetrics, that.sonarMetrics);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectid, projectName, revision, couplingAndCohesionMetrics, sonarMetrics);
    }
}
