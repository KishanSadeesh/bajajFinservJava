package com.vitvellore.bajaj_test;

// This class helps us send the answer back
public class SubmissionData {
    private String finalQuery;

    public SubmissionData(String finalQuery) {
        this.finalQuery = finalQuery;
    }

    public String getFinalQuery() {
        return finalQuery;
    }
}