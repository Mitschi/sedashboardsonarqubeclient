package com.github.mitschi.sedashboard;

import java.util.Objects;

public class SonarConnectionDetails {
    private String databaseConnection; //jdbc/....
    private String username;
    private String password;

    public SonarConnectionDetails(){}


    public SonarConnectionDetails(String databaseConnection, String username, String password) {
        this.databaseConnection = databaseConnection;
        this.username = username;
        this.password = password;
    }

    public String getDatabaseConnection() {
        return databaseConnection;
    }

    public void setDatabaseConnection(String databaseConnection) {
        this.databaseConnection = databaseConnection;
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


    @Override
    public String toString() {
        return "SonarConnectionDetails{" +
                "databaseConnection='" + databaseConnection + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SonarConnectionDetails that = (SonarConnectionDetails) o;
        return Objects.equals(databaseConnection, that.databaseConnection) &&
                Objects.equals(username, that.username) &&
                Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(databaseConnection, username, password);
    }
}
