package com.analysetool.util;

public class Problem {

    //A number representing the severity of a Problem. Higher Number -> More urgent Problem
    private int severity;
    //A description of the problem.
    private String description;
    //Describes the affected Area as a String, such as "UniStat" meaning that the Problem lies in the UniStats
    private String affectedArea;
    //Optional, but if potential solutions were found.
    private String suggestedSolutions = "none";

    private String fullSolutionLink = "none";

    public Problem(int severity, String description, String affectedArea, String suggestedSolutions, String fullSolutionLink) {
        this.severity = severity;
        this.description = description;
        this.affectedArea = affectedArea;
        this.suggestedSolutions = suggestedSolutions;
        this.fullSolutionLink = fullSolutionLink;
    }

    public Problem(int severity, String description, String affectedArea, String suggestedSolutions) {
        this.severity = severity;
        this.description = description;
        this.affectedArea = affectedArea;
        this.suggestedSolutions = suggestedSolutions;
    }

    public Problem(int severity, String description, String affectedArea) {
        this.severity = severity;
        this.description = description;
        this.affectedArea = affectedArea;
    }

    public int getSeverity() {
        return severity;
    }

    public void setSeverity(int severity) {
        this.severity = severity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSuggestedSolutions() {
        return suggestedSolutions;
    }

    public void setSuggestedSolutions(String suggestedSolutions) {
        this.suggestedSolutions = suggestedSolutions;
    }

    public String getAffectedArea() {
        return affectedArea;
    }

    public void setAffectedArea(String affectedArea) {
        this.affectedArea = affectedArea;
    }

    public String getFullSolutionLink() {
        return fullSolutionLink;
    }

    public void setFullSolutionLink(String fullSolutionLink) {
        this.fullSolutionLink = fullSolutionLink;
    }

    @Override
    public String toString() {
        return "Problem{" +
                "severity=" + severity +
                ", description='" + description + '\'' +
                ", affectedArea='" + affectedArea + '\'' +
                ", suggestedSolutions='" + suggestedSolutions + '\'' +
                '}';
    }
}
