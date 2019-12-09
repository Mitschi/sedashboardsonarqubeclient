package com.github.mitschi.sedashboard;

import java.util.Objects;

public class SonarConnectionDetails {
    private String databaseConnection; //jdbc/....
    private String username;
    private String password;
    private String sonarURL = "http://localhost:9000";

    public SonarConnectionDetails(){}
    public SonarConnectionDetails(String databaseConnection, String username, String password, String sonarURL) {
        this.databaseConnection = databaseConnection;
        this.username = username;
        this.password = password;
        this.sonarURL = sonarURL;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSonarURL() {
        return sonarURL;
    }

    public void setSonarURL(String sonarURL) {
        this.sonarURL = sonarURL;
    }

    public String getDatabaseConnection() {
        return databaseConnection;
    }

    public void setDatabaseConnection(String databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SonarConnectionDetails that = (SonarConnectionDetails) o;
        return Objects.equals(databaseConnection, that.databaseConnection) &&
                Objects.equals(username, that.username) &&
                Objects.equals(password, that.password) &&
                Objects.equals(sonarURL, that.sonarURL);
    }

    @Override
    public int hashCode() {
        return Objects.hash(databaseConnection, username, password, sonarURL);
    }

    @Override
    public String toString() {
        return "SonarConnectionDetails{" +
                "databaseConnection='" + databaseConnection + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", sonarURL='" + sonarURL + '\'' +
                '}';
    }
}
