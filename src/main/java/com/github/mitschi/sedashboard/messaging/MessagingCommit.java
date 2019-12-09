package com.github.mitschi.sedashboard.messaging;

import java.io.Serializable;
import java.util.Objects;

public class MessagingCommit implements Serializable {

    private String projectName;
    private final String cloneUrl;
    private Long id;
    private final String commitId;
    private final String parentCommitId;

    public MessagingCommit(String projectName, String cloneUrl, Long id, String commitId, String parentCommitId) {
        this.projectName = projectName;
        this.cloneUrl = cloneUrl;
        this.id = id;
        this.commitId = commitId;
        this.parentCommitId = parentCommitId;
    }

    public String getCloneUrl() {
        return cloneUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCommitId() {
        return commitId;
    }

    public String getParentCommitId() {
        return parentCommitId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MessagingCommit)) return false;
        MessagingCommit that = (MessagingCommit) o;
        return Objects.equals(projectName, that.projectName) &&
                Objects.equals(cloneUrl, that.cloneUrl) &&
                Objects.equals(id, that.id) &&
                Objects.equals(commitId, that.commitId) &&
                Objects.equals(parentCommitId, that.parentCommitId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectName, cloneUrl, id, commitId, parentCommitId);
    }

    @Override
    public String toString() {
        return "MessagingCommit{" +
                "projectName='" + projectName + '\'' +
                ", cloneUrl='" + cloneUrl + '\'' +
                ", id=" + id +
                ", commitId='" + commitId + '\'' +
                ", parentCommitId='" + parentCommitId + '\'' +
                '}';
    }
}